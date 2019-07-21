#!/usr/bin/env groovy
pipeline {
	agent any
	options {
      		buildDiscarder(logRotator(numToKeepStr: '3'))
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
