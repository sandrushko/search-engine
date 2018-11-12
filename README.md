## Sharded Search Engine

# Content:
* 1. Start shards
* 2. Start server
* 3. Server REST API
* 4. Client console app
* 5. 

# 1. Start shards

Shard 1:
```bash
sbt -J-Dconfig.file=src/main/resources/shard/application.conf -J-Dakka.remote.netty.tcp.port=7780 "runMain searchengine.sharding.ShardApp"
```

Shard 2:
```bash
sbt -J-Dconfig.file=src/main/resources/shard/application.conf -J-Dakka.remote.netty.tcp.port=7781 "runMain searchengine.sharding.ShardApp"
```

# 2. Start server
```bash
sbt -J-Dconfig.file=src/main/resources/server/application.conf "runMain searchengine.server.ServerApp"
```

# 3. Server REST API

Put text:
* POST /text/:key
Success Response:
Code: 200
Content: {"key": "some-key"}

Get text:
* GET /text/:key
Success Response:
Code: 200
Content: {"key": "some-key", "text": "some text"}

Search:
* GET /text?search=search text
Success Response:
Code: 200
Content: {"keys": ["key1", "key2"]}

# 3. Client console app
```bash
sbt -J-Dconfig.file=src/main/resources/client/application.conf "runMain searchengine.client.ClientApp"
```
Commands:
* 1. put
Sample: 
```bash
>put 'key' 'some text'
```
* 2. get
Sample: 
```bash
>get 'key'
```
* 3. search
Sample: 
```bash
>search 'search text'
```
