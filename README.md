# LogWatchAi

Ein lokales Java-Tool überwacht Logdateien, analysiert
neue Einträge in Echtzeit mithilfe von KI und leitet
automatisch passende Maßnahmen oder Warnungen


Projekt neu erstellen:
ind der konsole im verzecihniss mit dem pom.xml:
$ .\mvnw.cmd clean package
$ docker-compose down   //evtl. mit -v (löscht volumes) nur wenn datenbank weggeworfen werden kann!!!
$ docker-compose build
$ docker-compose up -d


Projket mit loggenerattor
$ cd logwatchai
$ .\mvnw.cmd clean package
$ cd loggenerator
$ .\mvnw.cmd clean package
$ cd ..\logwatchai
$ docker-compose up --build

