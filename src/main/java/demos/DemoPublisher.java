package demos;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DemoPublisher {
  private static final String PROJECT_KEY = "project";
  private static final String TOPIC_KEY = "topic";
  private static final int NUM_MESSAGES = 1000;

  public static void main(String... args) throws Exception {
    System.out.println("Received command line args: " + Arrays.toString(args));

    Map<String, String> parsed = parseArgs(args);
    System.out.println("Parsed into: " + parsed);

    if (!parsed.containsKey(PROJECT_KEY)) {
      throw new IllegalArgumentException("Missing arg: " + PROJECT_KEY);
    }
    if (!parsed.containsKey(TOPIC_KEY)) {
      throw new IllegalArgumentException("Missing arg: " + TOPIC_KEY);
    }

    publish(parsed.get(PROJECT_KEY), parsed.get(TOPIC_KEY));
  }

  private static Map<String, String> parseArgs(String... args) {
    Map<String, String> parsed = new HashMap<>();
    for (String arg : args) {
      int idx = arg.indexOf('=');
      if (idx == -1) {
        throw new IllegalArgumentException("All args must be passed as key=value");
      } else if (idx == arg.length() - 1) {
        System.out.println("Found arg with empty value: " + arg);
        continue;
      }

      String value = arg.substring(idx + 1);
      if (arg.startsWith(PROJECT_KEY)) {
        parsed.put(PROJECT_KEY, value);
      }
      else if (arg.startsWith(TOPIC_KEY)) {
        parsed.put(TOPIC_KEY, value);
      } else {
        System.out.println("Found unrecognized arg: " + arg);
      }
    }
    return parsed;
  }

  private static void publish(String project, String topic) throws Exception {
    System.out.printf("Publishing to %s in %s%n", topic, project);

    TopicName topicName = TopicName.of(project, topic);
    Publisher publisher = null;

    try {
      publisher = Publisher.newBuilder(topicName).build();

      System.out.printf("Sending %d messages to %s%n", NUM_MESSAGES, topic);
      for (SongUnion song : AvailableSongs.generateMessages(NUM_MESSAGES)) {
        PubsubMessage message = PubsubMessage.newBuilder().setData(song.asByteString()).build();
        ApiFuture<String> future = publisher.publish(message);

        ApiFutures.addCallback(
            future,
            new ApiFutureCallback<>() {
              @Override
              public void onFailure(Throwable t) {
                System.out.println(t.toString());
              }

              @Override
              public void onSuccess(String result) {
                System.out.println("Successfully published message");
              }
            },
            MoreExecutors.directExecutor());
      }
    } finally {
      if (publisher != null) {
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      }
      System.out.println("Finished...");
    }
  }
}
