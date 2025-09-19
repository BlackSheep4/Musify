pipeline {
    agent any

    environment {
        spotifyClientCredentialsId = "spotify-client-id"
        spotifySecretCredentialsId = "spotify-client-secret"
        imageName = "spotify-downloader"
        dockerfilePath = "Dockerfile"
    }

    stages {
        stage('Build package') {
            steps {
                script {
                    echo 'Building package...'
                    sh "docker build -t ${imageName} -f ${dockerfilePath} ."
                }
            }
        }
        stage('Run tests') {
            steps {
                script {
                    echo 'Running tests...'
                    sh "docker run ${imageName} --version"
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    echo 'Loading credentials...'
                    withCredentials([
                        string(credentialsId: 'spotify-client-id', variable: 'SPOTIFY_CLIENT_ID'),
                        string(credentialsId: 'spotify-client-secret', variable: 'SPOTIFY_CLIENT_SECRET')
                    ]) {
                        echo 'Deploying package...'
                        sh """
                        docker run --rm \\
                          -v ${env.WORKSPACE}/data:/music \\
                          -v ${env.WORKSPACE}/config/cookies.txt:/config/cookies.txt \\
                          ${imageName} '${params.SPOTIFY_URL}' \\
                          --client-id '${SPOTIFY_CLIENT_ID}' \\
                          --client-secret '${SPOTIFY_CLIENT_SECRET}' \\
                          --cookie-file /config/cookies.txt \\
                          --format m4a \\
                          --bitrate disable
                        """
                    }
                }
            }
        }
        stage('Verify') {
            steps {
                script {
                    echo "Verifying downloaded files..."
                    sh "ls -lh ${env.WORKSPACE}/data"
                }
            }
        }
        stage('Zip files') {
            steps {
                script {
                    echo 'Zipping downloaded files...'
                    def timestamp = sh(script: "date +%Y-%m-%d_%H-%M", returnStdout: true).trim()
                    sh "zip -r downloaded-music-${timestamp}.zip data"
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment successful!'
            archiveArtifacts artifacts: 'data/**', fingerprint: true
            archiveArtifacts artifacts: '*.zip', fingerprint: true
        }
    }
}