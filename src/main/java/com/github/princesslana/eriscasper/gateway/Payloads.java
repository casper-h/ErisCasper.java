package com.github.princesslana.eriscasper.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.EventFactory;
import com.github.princesslana.eriscasper.data.immutable.Wrapped;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.rx.Maybes;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Optional;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Payloads {

  private static final Logger LOG = LoggerFactory.getLogger(Payloads.class);

  private ObjectMapper jackson;

  public Payloads(ObjectMapper jackson) {
    this.jackson = jackson;
  }

  public <T> Single<T> dataAs(Payload p, Class<T> clazz) {
    return p.d(jackson, clazz);
  }

  public Payload heartbeat(Optional<SequenceNumber> s) {
    return ImmutablePayload.builder().op(OpCode.HEARTBEAT).d(s.map(jackson::valueToTree)).build();
  }

  public Payload identify(BotToken token) {
    return identify(ImmutableIdentify.builder().token(token).build());
  }

  public Payload identify(Identify id) {
    return ImmutablePayload.builder().op(OpCode.IDENTIFY).d(jackson.valueToTree(id)).build();
  }

  public Single<Payload> read(String text) {
    return Single.fromCallable(() -> jackson.readValue(text, Payload.class));
  }

  public Payload resume(Resume r) {
    return ImmutablePayload.builder().op(OpCode.RESUME).d(jackson.valueToTree(r)).build();
  }

  public Maybe<Event> toEvent(Payload payload) {
    return Single.just(payload)
        .filter(Payload.isOp(OpCode.DISPATCH))
        .flatMap(p -> Maybes.fromOptional(p.t()))
        .map(et -> EventFactory.forType(et).create(payload.d().get()))
        .doOnError(t -> LOG.warn("Unable to convert payload to event: {}", payload, t))
        .onErrorComplete();
  }

  public Single<String> writeToString(Payload p) {
    return Single.fromCallable(() -> jackson.writeValueAsString(p));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/topics/gateway#identify">
   *     https://discordapp.com/developers/docs/topics/gateway#identify</a>
   */
  // TODO: This structure is not complete
  @Value.Immutable
  @JsonDeserialize(as = ImmutableIdentify.class)
  public static interface Identify {
    BotToken getToken();

    default ConnectionProperties getProperties() {
      return ConnectionProperties.ofDefault();
    }
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties">
   *     https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties</a>
   */
  @Value.Immutable
  public static interface ConnectionProperties {
    @JsonProperty("$os")
    String getOs();

    @JsonProperty("$browser")
    String getBrowser();

    @JsonProperty("$device")
    String getDevice();

    public static ConnectionProperties ofDefault() {
      return ImmutableConnectionProperties.builder()
          .os(System.getProperty("os.name"))
          .browser("ErisCasper.java")
          .device("ErisCasper.java")
          .build();
    }
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/topics/gateway#resume">
   *     https://discordapp.com/developers/docs/topics/gateway#resume</a>
   */
  @Value.Immutable
  public static interface Resume {
    BotToken getToken();

    @JsonProperty("session_id")
    SessionId getSessionId();

    SequenceNumber getSeq();
  }

  @Value.Immutable
  @Wrapped
  public static interface SessionIdWrapper extends Wrapper<String> {}
}
