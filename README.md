# 🎧 Musify

**Musify** is a Dockerized Spotify downloader pipeline powered by `spotDL` and integrated with Jenkins. It securely automates the download of Spotify tracks in high-quality formats using your own credentials and cookies.

## 🚀 Features

- 🔄 CI/CD pipeline with Jenkins
- 🐳 Dockerized environment
- 🎵 Spotify track download via `spotDL`
- 🔐 Credentials managed securely with Jenkins secrets
- 📂 Volume mapping for downloaded music and cookies
- ⚙️ Customizable parameters (track URL, credentials)

## 📦 Requirements

- [Docker](https://www.docker.com/)
- [Jenkins](https://www.jenkins.io/)
- Spotify Developer Account (for Client ID and Secret)
- Valid `cookies.txt` file (optional for some downloads)

## 🛠️ Usage

1. **Clone the repo**
   ```bash
   git clone https://github.com/your-username/musify.git
   cd musify
   ```

2. **Configure Jenkins**
   - Add the following credentials to Jenkins:
     - `spotify-client-id` → your Spotify app's Client ID
     - `spotify-client-secret` → your Spotify app's Client Secret
   - Configure your pipeline with `SPOTIFY_URL` as a string parameter.

3. **Run the pipeline**
   - In Jenkins, trigger the pipeline and enter the Spotify track URL when prompted. Example URL:
     ```
     https://open.spotify.com/track/3JOXwHPhtdRU3kYKcf64Gj
     ```
   - The pipeline will:
     1. Build the Docker image
     2. Test that `spotDL` works
     3. Run the container to download the track

## 🧪 Example Jenkinsfile Snippet

```groovy
pipeline {
    agent any

    parameters {
        string(name: 'SPOTIFY_URL', defaultValue: '', description: 'Spotify track URL')
    }

    stages {
        stage('Build package') {
            steps {
                sh "docker build -t spotify-downloader -f Dockerfile ."
            }
        }
        stage('Run tests') {
            steps {
                sh "docker run spotify-downloader --version"
            }
        }
        stage('Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'spotify-client-id', variable: 'SPOTIFY_CLIENT_ID'),
                    string(credentialsId: 'spotify-client-secret', variable: 'SPOTIFY_CLIENT_SECRET')
                ]) {
                    sh """
                    docker run --rm \
                      -v ${env.WORKSPACE}/data:/music \
                      -v ${env.WORKSPACE}/config/cookies.txt:/config/cookies.txt \
                      spotify-downloader '${params.SPOTIFY_URL}' \
                      --client-id '${SPOTIFY_CLIENT_ID}' \
                      --client-secret '${SPOTIFY_CLIENT_SECRET}' \
                      --cookie-file /config/cookies.txt \
                      --format m4a \
                      --bitrate disable
                    """
                }
            }
        }
    }
}
```

## 📁 Directory Structure

```
musify/
├── Dockerfile
├── Jenkinsfile
├── config/
│   └── cookies.txt
├── data/
└── README.md
```

## 📝 License

MIT — feel free to use, fork, and improve!

## ❤️ Credits

Built with [spotDL](https://github.com/spotDL/spotify-downloader) and lots of ☕ by Álvaro.

