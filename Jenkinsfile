pipeline {
    agent any 
    environment {
        GIT_MESSAGE = sh(script: 'git log $COMMIT --format=format:%s -1', , returnStdout: true).trim()
        GIT_AUTHOR  = sh(script: 'git log $COMMIT -1 --pretty=format:%an', , returnStdout: true).trim()
        GIT_REPO_NAME = env.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')
    }
    stages {
        stage ('Pre Build: Prepare Jenkins Env') {
            when {
              anyOf {
                branch "none"
                    } 
            }
            steps {
                script {
                    sh 'echo ""'
                }
            }
        }
        stage('Build') {
            when {
              anyOf {
                branch 'master'
                branch "feature-*"
                    }
            }
            steps {
                
                withMaven(
                  maven: 'maven-3.6.3') {
                  sh "mvn -Dmaven.repo.local=~/m2 --batch-mode -f pom.xml clean verify package"
                        }
                sh 'echo $BRANCH_NAME'
                sh 'echo $GIT_REPO_NAME'
                script {

                  // It's necessary to define where is the docker that should build the image
                  def dockerHome = tool 'default'
                  env.PATH = "${dockerHome}/bin:${env.PATH}"
          
                  // Build the docker image environment/application:build                
                  docker.build("${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID}", "--network host .")
                        }
            }
        }

        stage('Tests') {
            when {
              anyOf {
                branch 'master'
                branch "feature-*"
                    }
            }
            steps {
                sh 'echo test'
            }
        }

        stage('Feature Branch Deploy') {
            when {
                    branch "feature-*" 
            }
            steps {

                 // Upload the build to ECR
                sh 'aws ecr describe-repositories --repository-names $GIT_REPO_NAME/$BRANCH_NAME || aws ecr create-repository --repository-name $GIT_REPO_NAME/$BRANCH_NAME'
                sh '$(aws ecr get-login)'
                script {
                  docker.withRegistry('https://881590245028.dkr.ecr.eu-central-1.amazonaws.com/${env.GIT_REPO_NAME}/${env.BRANCH_NAME}') {
                    docker.image("${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID}").push('latest')
                    docker.image("${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID}").push(env.BUILD_ID)
                  }
                }

                  // Apply the files to K8s
                  sh 'cd /var/lib/jenkins ; rm -rf helm-charts ; GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no" git clone git@gitlab.com:bonify-devops/helm-charts.git'
                  sh 'cd /var/lib/jenkins/helm-charts ; ./feature_deploy-stgv2.sh $GIT_REPO_NAME $BRANCH_NAME'

                  // Update K8s deployment with the new ECR image
                sh "kubectl -n $GIT_REPO_NAME-$BRANCH_NAME set image deployment $GIT_REPO_NAME-$BRANCH_NAME=881590245028.dkr.ecr.eu-central-1.amazonaws.com/$GIT_REPO_NAME/$BRANCH_NAME:$BUILD_ID --all"



            }
        }

       stage('Stage Deploy') {
            when {
                branch 'master'
            }
            steps {
                // Check if ECR repo already exists, if it doesn't, create it
                sh '$(aws ecr get-login)'
                sh 'aws ecr describe-repositories --repository-names $GIT_REPO_NAME/$BRANCH_NAME || aws ecr create-repository --repository-name $GIT_REPO_NAME/$BRANCH_NAME'
        
                // Upload the new builded docker image to ECR 
                script {
                  docker.withRegistry('https://881590245028.dkr.ecr.eu-central-1.amazonaws.com/${env.GIT_REPO_NAME}/${env.BRANCH_NAME}') {
                    docker.image("${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID}").push('latest')
                    docker.image("${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID}").push(env.BUILD_ID)
                  }

                // Tag Build
                // sh 'git tag $BUILD_ID'
                // sh 'git push origin --tags'

                // Update K8s deployment with the new ECR image
                sh "kubectl -n stg-${env.GIT_REPO_NAME} set image deployment stg-${env.GIT_REPO_NAME}=881590245028.dkr.ecr.eu-central-1.amazonaws.com/${env.GIT_REPO_NAME}/${env.BRANCH_NAME}:${env.BUILD_ID} --all"

                }
            }
        }

        stage('Prod Deploy') {
            when {
                branch '----master'
            } 
            steps {
                sh 'echo test'
//                input message: 'Finished using the web site? (Click "Proceed" to continue)'
            }
        }

    }

  post {
        always {
          script {
                def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']
                slackSend   message: "*${currentBuild.currentResult}:* ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.RUN_DISPLAY_URL}|Open>) - ${env.GIT_MESSAGE} - started by ${env.GIT_AUTHOR}",
                    baseUrl: "https://joonko-io.slack.com/services/hooks/jenkins-ci/",
                    channel: "jenkins",
                    color: COLOR_MAP[currentBuild.currentResult],
                    tokenCredentialId: "slack"
                  }
              }
          }

}
