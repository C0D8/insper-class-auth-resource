pipeline{
    agent any
    stages{
        stage('Jenkis Auth'){
            steps{
                echo 'Jankins auth interface'
            }
        }
        stage('Build Interface'){
            steps{
                build job: 'store.auth', wait: true
            }
        }

        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
        }

        stage('Build Image'){

            steps{
                script{
                    auth = docker.build("c0d8/auth:${env.BUILD_ID}", "-f Dockerfile .")
                }
            }

        }

        stage('Push Image'){
            steps{
                script{
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-credential'){
                        auth.push("${env.BUILD_ID}")
                        auth.push("latest")
                    }
                }
            }
        }
       
    }
}