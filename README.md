Sharded Search Engine

Content:
*1. Start shards
*2. Start server
*3. Server REST API

*1. Start shards

Shard 1:
$ sbt -J-Dconfig.file=src/main/resources/shard/application.conf -J-Dakka.remote.netty.tcp.port=7780 "runMain searchengine.sharding.ShardApp"
Shard 2:
$ sbt -J-Dconfig.file=src/main/resources/shard/application.conf -J-Dakka.remote.netty.tcp.port=7781 "runMain searchengine.sharding.ShardApp"

*2. Start server

$ sbt -J-Dconfig.file=src/main/resources/server/application.conf "runMain searchengine.server.ServerApp"

*3. Server REST API

