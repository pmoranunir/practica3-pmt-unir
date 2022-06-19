pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
                
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'results/coverage', reportFiles: 'index.html', reportName: 'HTML Report - Coverage', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'results', reportFiles: 'unit_result.html', reportName: 'HTML Report - Unit Test', reportTitles: ''])
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/api_result.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'results', reportFiles: 'api_result.html', reportName: 'HTML Report - API Test', reportTitles: ''])
            }
        }
        
        stage('e2e tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/cypress_result.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'results', reportFiles: 'cypress_result.html', reportName: 'HTML Report - e2e Test', reportTitles: ''])
            }
        }
    }
    post {
        success {
            echo "El trabajo ${JOB_NAME} Build: ${BUILD_NUMBER} se ha completado satisfactoriamente."
        }
        always {
            junit 'results/*_result.xml'
            cleanWs()
        }

        failure {
            echo "ERROR - El trabajo ${JOB_NAME} Build: ${BUILD_NUMBER} NO se ha completado satisfactoriamente."
            /*
            emailext body: "ERROR - El trabajo ${JOB_NAME} Build: ${BUILD_NUMBER} NO se ha completado satisfactoriamente.",
                subject: "Pipeline error", 
                to: "devops_practica3@unir.net"
            */
        }
    }
}
