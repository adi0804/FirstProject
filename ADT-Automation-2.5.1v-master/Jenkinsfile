#!/usr/bin/env groovy

@Library("com.optum.jenkins.pipeline.library@master") _
def gitHubToken       = 'rkris22'
def DeployFlg = 'True'
def jarfile = ''

pipeline {
    agent {label 'docker-maven-slave'}

    environment 
    {
        // Credentials
        MULTIPURPOSE_CREDENTIALS = 'cdsmdev'
        SCP_CREDS = "cdsmdev" // TODO: use wesbuser
        SCP_USER = "cdsmdev" // TODO: use wesbuser
        
	    
        // Other
		//JAVA_VERSION = "1.8.0"
		//MAVEN_VERSION = "4.0.0"
	}

    options {
      buildDiscarder(logRotator(numToKeepStr: '3'))
    }
    stages {
      stage ('Build') {
            steps {
                //echo "Artifact version is ${env.ARTIFACT_VERSION}"
                initializeBranchSpecificVars()
                determineArtifactVersion()
                echo "Artifact version is ${env.PIPELINE_VERSION}"
		
                glMavenBuild mavenGoals: "-B -f pom.xml clean org.jacoco:jacoco-maven-plugin:0.7.4.201502262128:prepare-agent install dependency:copy-dependencies"
                //archive includes: '**/target/*.jar,**/target/*.war'
            }
      }
	
	//stage('Artifactory') {
            //when {
            //    expression { env.BRANCH_NAME == "master" || env.BRANCH_NAME == "stg" || env.BRANCH_NAME == "tst" || env.BRANCH_NAME == "dev" }
                //branch 'dev'
          //  }
            //steps {
                // pass deploy at end to upload all the artifacts
              //  glMavenArtifactoryDeploy deployAtEnd: true, artifactoryUserCredentialsId:"${env.MULTIPURPOSE_CREDENTIALS}"
            //}
        //}
     
	 stage('Sonar') {
		
					//when {
					  //  expression { env.BRANCH_NAME == "master" || env.BRANCH_NAME == "dev" }
						//branch 'dev'
				   //}
				steps {
							echo 'Sonar Scan'
							glSonarMavenScan gitUserCredentialsId: "$gitHubToken"
						}
					}
     
	stage ('Approve Deploy'){ 
		steps { 
		script { 
          try { glApproval  time:1, unit:'MINUTES' , message: 'Approve Dev deployment ?' } 
          catch ( e ) { 
                              DeployFlg     = 'False' 
					} 
				} 
			} 
		}
	 
	//stage('Deploy') {
	//	when { expression { ("$DeployFlg" == 'True') }  }
	//	steps {
	//		echo 'Deploying..'
			
	//		deployCode()		
		   
	//		}
        //}

    }
    post {
        always {
            echo 'This will always run'
            emailext body:  "Build URL: ${BUILD_URL}",
                    subject: "$currentBuild.currentResult-$JOB_NAME",
                    to: 'raghu2.krishna@optum.com',
	    from: 'raghu2.krishna@optum.com'
        }
        success {
            echo 'This will run only if successful'
        }
        failure {
            echo 'This will run only if failed'
        }
        unstable {
            echo 'This will run only if the run was marked as unstable'
        }
        changed {
            echo 'This will run only if the state of the Pipeline has changed'
            echo 'For example, if the Pipeline was previously failing but is now successful'
        }
    }
}


TARGET_SERVER = ""
PIPELINE_ENV = ""
PIPELINE_SNAPSHOT = ""

// Fucntion to set the branch specfic values
def initializeBranchSpecificVars() {
	echo "Branch is ${env.BRANCH_NAME}"
	if(env.BRANCH_NAME == "master") {
		PIPELINE_ENV = ""
		TARGET_SERVER = ""
		PIPELINE_SNAPSHOT = ""
	}					
	else if(env.BRANCH_NAME == "stg") {
		PIPELINE_ENV = "STG_"
		TARGET_SERVER = "apsrd8844"
		PIPELINE_SNAPSHOT = "-SNAPSHOT"
		SCP_CREDS = "rkris22"
	}
	else if(env.BRANCH_NAME == "TestJenkins") {
		PIPELINE_ENV = "TST_"
		TARGET_SERVER = "apsrd8844"
		PIPELINE_SNAPSHOT = "-SNAPSHOT"
		SCP_CREDS = "rkris22"
	}
	else {
		PIPELINE_ENV = "DEV_"
		TARGET_SERVER = "apsrd8844"
		PIPELINE_SNAPSHOT = "-SNAPSHOT"
		SCP_CREDS = "rkris22"
	}
}

//Function to determine version of artifact being generated, combination of pom version & build number
def determineArtifactVersion() {
	//def pomText = readFile('pom.xml')
    //def version = "${PIPELINE_ENV}" + (new groovy.util.XmlSlurper().parseText("${pomText}").version.toString().split('-').first()) + "${PIPELINE_SNAPSHOT}"
    //For some unknown reason, setting artifact version works in maven only when set using a env variable like below
	 def pom = readMavenPom file: 'pom.xml'
         def version = pom.version
    env.PIPELINE_VERSION = version
}

def deployCode(){
	withCredentials([usernamePassword(credentialsId: 'cdsm_adm', passwordVariable: 'pwd', usernameVariable: 'user')]) {
		   		def remote = [:]
				remote.name = 'apsrd8844'
				remote.host = 'apsrd8844.uhc.com'
				remote.user = env.user
				remote.password = env.pwd
				remote.allowAnyHosts = true
				//sshPut remote: remote, from: 'target/adtautomation.jar'  , into: '/home/cdsm_adm/QAA/Jars_Offshore/ADT/dev'
				}
	}
