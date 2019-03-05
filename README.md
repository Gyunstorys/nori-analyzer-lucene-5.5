# nori-analyzer-lucene-5.5
Neo4j에서 lucene 5.5를 지원하여 lucene 5.5에 맞게 소스를 수정하였습니다.
FST파일 구조가 달라졌는지 최신 사전을 읽지못하여 사전 Rebuild 진행하여 정상동작하는것을 확인하였습니다.

## 환경
* Java 1.8 Version
* maven 3
* Lucene 5.5

## Build
* lucene 5.5버전의 nori analyzer를 build
* git clone https://github.com/Gyunstorys/nori-analyzer-lucene-5.5
* mvn install
