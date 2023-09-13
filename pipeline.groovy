pipeline {
    agent any
    options
    {
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    environment {
                    // SHORT_COMMIT = "${GIT_COMMIT[0..7]}"
                    APP_NAME="${JOB_NAME.substring(0, JOB_NAME.lastIndexOf('/'))}"
                    SHORT_COMMIT="${GIT_COMMIT[0..7]}"
                    ECR_URL = "091457645177.dkr.ecr.ap-southeast-1.amazonaws.com"
                    ECR_REPO_NGINX = "comana_nginx"
                    ECR_REPO_APP = "comana_app"
                    AWS_CREDENTIALS_ID = "aws_credentials"
                    ENV_FILE_CREDENTIAL_ID = "env_comana"
                    //SHORT_COMMIT="$GIT_COMMIT"
    }
    stages {
        stage('Clone repository') {
            steps {
                script{
                checkout scm
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                sh "echo $APP_NAME"
                echo 'Running pytest..'
            }
        }
        stage('Build nginx') {
            // when {
            //     changeset "**/docker/*"
            // }
            steps {
                script {
                docker.withRegistry("https://" + "${env.ECR_URL}", 'ecr:ap-southeast-1:aws_credentials') {
                        def IMAGE_NAME="${env.ECR_URL}/${env.ECR_REPO_NGINX}:${env.SHORT_COMMIT}"
                        def customImage = docker.build("$IMAGE_NAME", "-f Dockerfile.nginx .")
                        customImage.push()
                }
                }
            }
            }
        // stage('Update image nginx') {
        //     // when {
        //     //     expression {
        //     //         // Only run 'Test' stage if the 'Build' stage was successful
        //     //         return currentBuild.result = "SUCCESS"
        //     //     }
        //     // }
        //     steps {
        //         script {
        //             // SSH key credentials ID from Jenkins Global Credentials
        //             def sshKeyCredentials = 'ssh_deploy'
        //             def IMAGE_APP_NAME="${env.ECR_URL}/${env.ECR_REPO_APP}:${env.SHORT_COMMIT}"
        //             def IMAGE_NGINX_NAME="${env.ECR_URL}/${env.ECR_REPO_NGINX}:${env.SHORT_COMMIT}"
        //             def dockerComposeFile = 'docker-compose.prod.yml'
        //             // SSH to the remote host and update the Docker image
        //             sshagent(credentials: [sshKeyCredentials]) {
        //                     sh """
        //                         [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
        //                         ssh-keyscan -t rsa,dsa 192.168.1.58 >> ~/.ssh/known_hosts
        //                         ssh root@192.168.1.58 "
        //                             set -e;
        //                             cd /data/comana;
        //                             hostname;
        //                             ls -la;
        //                             sed s/tag_nginx/'${env.SHORT_COMMIT}'/g docker-compose-example.yml > ${dockerComposeFile}"
        //                         """
        //             }
        //         }
        //     }
        // }
        stage('Build app') {
            steps {
                withCredentials([string(credentialsId: 'rails_master_key', variable: 'RAILS_MASTER_KEY')]) {
                    sh 'echo $RAILS_MASTER_KEY > a.txt'
                    sh 'cat a.txt || true'
                withCredentials([file(credentialsId: "${env.ENV_FILE_CREDENTIAL_ID}", variable: 'ENV_FILE')]) {
                script {
                    sh 'cat $ENV_FILE > .env'
                    docker.withRegistry("https://" + "${env.ECR_URL}", 'ecr:ap-southeast-1:aws_credentials') {
                        def IMAGE_NAME="${env.ECR_URL}/${env.ECR_REPO_APP}:${env.SHORT_COMMIT}"
                        def customImage = docker.build("$IMAGE_NAME", "--build-arg RAILS_MASTER_KEY=$RAILS_MASTER_KEY -f Dockerfile.app.prod .")
                        customImage.push()
                }
                }
                }
            }
            }
        }
        stage('Update image & deploy new version') {
            // when {
            //     expression {
            //         // Only run 'Test' stage if the 'Build' stage was successful
            //         return currentBuild.result == 'SUCCESS'
            //     }
            // }
            when {
                beforeAgent true
                beforeInput true
                branch 'prod'
            }
            options {
            timeout(time: 4, unit: 'MINUTES')
            }
            input {
                message "Do you want to proceed for production deployment?"
            }
            steps {
                script {
                    // SSH key credentials ID from Jenkins Global Credentials
                    def sshKeyCredentials = 'ssh_deploy'
                    def dockerComposeFile = 'docker-compose.prod.yml'
                    // SSH to the remote host and update the Docker image
                    sshagent(credentials: [sshKeyCredentials]) {
                            sh """
                                [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                                ssh-keyscan -t rsa,dsa 192.168.1.58 >> ~/.ssh/known_hosts
                                scp .env root@192.168.1.58:/data/comana/.env
                                ssh root@192.168.1.58 "
                                    set -e;
                                    cd /data/comana;
                                    sed s/tag_nginx/'${env.SHORT_COMMIT}'/g docker-compose-example.yml > ${dockerComposeFile};
                                    sed -i s/tag_app/'${env.SHORT_COMMIT}'/g ${dockerComposeFile};
                                    cat ${dockerComposeFile}
                                    /usr/bin/docker-compose down -v
                                    /usr/bin/docker-compose -f ${dockerComposeFile} up -d "
                                """
                    }
                }
            }
        }
        stage('Cleanup') {
            steps {
                echo 'Cleaning..'
                echo 'Running docker rmi..'
            }
        }
    }
    post {
        always {
            // Clean up Docker images and temporary .env file after the build is complete
            cleanWs()
        }
    }
}