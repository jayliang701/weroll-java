mvn deploy:deploy-file -Dmaven.test.skip=true -Dfile=./target/weroll-0.0.2-SNAPSHOT.jar -DgroupId=com.magicfish -DartifactId=weroll -Dversion=0.0.2-SNAPSHOT -Dpackaging=jar -DrepositoryId=snapshots -Durl=https://devrepo.devcloud.cn-east-3.huaweicloud.com/04/nexus/content/repositories/005f168280cf4937a6cf3cb35b78e12e_2_0/