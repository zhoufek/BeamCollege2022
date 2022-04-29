package demos;

import static java.util.stream.Collectors.toList;

import com.google.common.io.Resources;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AvailableSongs {
  private AvailableSongs() {}

  // Song lyrics pulled from This is Anfield and Anfield Online.
  private static final List<String> FILES = List.of(
      "Campione",
      "Fields of Anfield Road",
      "Jurgen Klopp",
      "Mane Mane Mane",
      "Mo Salah - The Egyptian King",
      "Poetry in Motion",
      "Steven Gerrard",
      "The Reds are Coming Up the Hill",
      "We All Live In a Red and White Kop",
      "We Love You Liverpool",
      "You'll Never Walk Alone"
  );
  private static final List<SongWithoutTime> ALL_SONGS = FILES.stream()
      .map(file -> {
        try {
          return SongWithoutTime.newBuilder()
              .setName(file)
              .setLyrics(Files.readString(Path.of(Resources.getResource(file).toURI())))
              .build();
        } catch (Exception e) {
          System.out.println("Failure: " + e.getMessage());
          System.exit(1);
        }
        return SongWithoutTime.getDefaultInstance();
      })
      .collect(toList());

  public static List<SongUnion> generateMessages(int num) {
    List<SongUnion> generated = new ArrayList<>();
    for (int i = 0; i < num; ++i) {
      generated.add(generateMessage());
    }
    return generated;
  }

  private static SongUnion generateMessage() {
    SongWithoutTime withoutTime = ALL_SONGS.get(new Random().nextInt(ALL_SONGS.size()));
    double selection = new Random().nextDouble();
    if (selection <= 0.98) {
      return SongUnion.of(Song.newBuilder()
          .setTimeSung(Instant.now().toString())
          .setName(withoutTime.getName())
          .setLyrics(withoutTime.getLyrics())
          .build());
    }
    return SongUnion.of(withoutTime.getLyrics());
  }
}
