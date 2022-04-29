package demos;

import com.google.protobuf.ByteString;
import javax.annotation.Nullable;

public final class SongUnion {
  private final @Nullable Song song;
  // This will completely fail to parse when attempting to read from the Pub/Sub topic.
  private final @Nullable String badSong;

  private SongUnion(@Nullable Song song, @Nullable String badSong) {
    this.song = song;
    this.badSong = badSong;
  }

  public static SongUnion of(Song song) {
    return new SongUnion(song, null);
  }

  public static SongUnion of(String badSong) {
    return new SongUnion(null, badSong);
  }

  public ByteString asByteString() {
    return song == null
        ? ByteString.copyFromUtf8(badSong)
        : song.toByteString();
  }

  @Override
  public String toString() {
    return song == null
        ? badSong
        : song.toString();
  }
}
