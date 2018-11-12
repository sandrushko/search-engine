## Sharded Search Engine

# Content:
1. Start shards
2. Start server
3. Server REST API
4. Client console app

# 1. Start shards

Shard 1:
```bash
sbt '; set javaOptions ++= Seq("-Dconfig.file=src/main/resources/shard/application.conf","-Dakka.remote.netty.tcp.port=7780"); runMain searchengine.sharding.ShardApp'
```

Shard 2:
```bash
sbt '; set javaOptions ++= Seq("-Dconfig.file=src/main/resources/shard/application.conf","-Dakka.remote.netty.tcp.port=7781"); runMain searchengine.sharding.ShardApp'
```

# 2. Start server

Setup shards:

```javascript
sharding {
  nodes: [
    "127.0.0.1:7780",
    "127.0.0.1:7781"
  ]
}
```

Setup REST endpoint:

```javascript
sharding {
  nodes: [
    "127.0.0.1:7780",
    "127.0.0.1:7781"
  ]
}
```

```bash
sbt '; set javaOptions ++= Seq("-Dconfig.file=src/main/resources/server/application.conf"); runMain searchengine.server.ServerApp'
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

# 4. Client console app
```bash
sbt '; set javaOptions ++= Seq("-Dconfig.file=src/main/resources/client/application.conf"); runMain searchengine.client.ClientApp'
```

Commands:
1. put

Sample: 
```bash
> put 'key' 'some text'
```
2. get

Sample: 
```bash
> get 'key'
```
3. search

Sample: 
```bash
> search 'search text'
```
