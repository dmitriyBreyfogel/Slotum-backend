pipeline {
  agent any

  stages {
    stage('Build Backend') {
      steps {
        bat 'ci\\build_backend.bat'
      }
    }

    stage('Smoke Startup') {
      steps {
        bat 'ci\\smoke_startup.bat'
      }
    }
  }
}
