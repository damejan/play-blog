package db;

import com.mongodb.client.MongoClients;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import models.Post;
import models.User;

public class MongoConfig {
    private static Datastore datastore;
    private static Config config;

    public static Datastore datastore() {
        if (datastore == null) {
            config = ConfigFactory.load();
            initDatastore(config.getString("mongodb_uri"), config.getString("mongodb_name"));
        }
        return datastore;
    }

    public static void initDatastore(String dbURI, String dbName) {
        datastore = Morphia.createDatastore(MongoClients.create(dbURI), dbName);
        datastore.getMapper().map(User.class, Post.class);
    }
}
