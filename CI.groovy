node "jenkins-slave", {
    container "common", {
        stage "Checkout sources", {
            checkout scm
        }

        stage "Build application", {
            sh "./gradlew clean assemble"
        }

        stage "Build and push Docker image", {
            docker.withRegistry "", "javatechnologies", {
                docker
                    .build("javatechnologies/penny-auction-service:latest")
                    .push()
            }
        }

        stage "Deploy", {
            sh "helm upgrade --recreate-pods --namespace default --tiller-namespace default -i penny-auction-service helm/penny-auction-service"
        }
    }
}