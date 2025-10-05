import java.text.SimpleDateFormat
def TODAY = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())
pipeline {
    agent any
    environment {
        strDockerTag = "${TODAY}_${BUILD_ID}"
        strDockerImage ="530hyelim/cicd_ypjp_api:${strDockerTag}"
        strGitUrl = "https://github.com/kh-powerpuffgirls/yoripick-joripick-api.git"
    }
    stages {
        // 1. 깃헙 체크아웃(master)
        stage('Checkout') {
            steps {
                git branch: 'jenkins', url: strGitUrl
            }
        }
        // 2. 소스코드 빌드
        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }
        // 3. 도커 이미지 빌드
        stage('Docker Image Build') {
            steps {
                script {
                    oDockImage = docker.build(strDockerImage, "--build-arg VERSION="+strDockerTag+" -f Dockerfile .")
                }
            }
        }
        // 4. 도커 이미지 푸쉬
        stage('Docker Image Push') {
            steps {
                script {
                    docker.withRegistry('', 'Dockerhub_Cred') {
                        oDockImage.push()
                    }
                }
            }
        }
        // 5. 프로덕션 서버 배포
        stage('Deploy Production') {
            steps {
                sshagent(credentials: ['SSH-PrivateKey']) {
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@3.38.213.177 docker container rm -f ypjp-api"
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@3.38.213.177 docker container run \
                        -d \
                        -p 8081:8081 -p 8443:8443 \
                        -v /app/upload:/app/upload \
                        -v /app/keystore.p12:/app/keystore.p12 \
                        --name=ypjp-api \
                        ${strDockerImage}"
                }
            }
        }
    }
}
