package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.data.Users;
import com.github.princesslana.eriscasper.data.event.MessageCreateEvent;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.channel.CreateMessageRequest;

public class EchoBot {
  public static void main(String args[]) {
    ErisCasper.create()
        .run(
            ctx ->
                ctx.getEvents()

                    // Same type as PingBot in examples
                    .ofType(MessageCreateEvent.class)
                    .map(MessageCreateEvent::unwrap)

                    // Need to check for bot's own message
                    .filter(d -> !Users.isBot(d.getAuthor()))
                    .filter(d -> d.getContent().map(c -> c.startsWith("+echo")).orElse(false))
                    .flatMapCompletable(
                        d -> {
                          String replyMessage =
                              d.getContent().map(c -> c.replaceFirst("\\+echo", "")).orElse("");

                          // Empty Arguments
                          if (replyMessage.trim().isEmpty()) {
                            replyMessage = "This command requires 1 argument";
                          }

                          return ctx.execute(
                                  RouteCatalog.createMessage(d.getChannelId()),
                                  CreateMessageRequest.ofText(replyMessage))
                              .toCompletable();
                        }));
  }
}
