#!/usr/bin/env groovy
pipeline {
	agent any
	  environment {
    //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
  }
	
	
	options {
      		buildDiscarder(logRotator(numToKeepStr: '3'))
    		}
}
	stages{
		stage('Build'){
			steps{
				echo "Build Stage"
			}
		}
		stage('Test'){
			steps{
				echo "Test Stage"
			}
		}
		stage('Deploy'){
			steps{
				echo "Deploy Stage"
			}
		}
}
}
