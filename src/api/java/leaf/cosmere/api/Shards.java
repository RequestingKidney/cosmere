package leaf.cosmere.api;

import leaf.cosmere.api.manifestation.Manifestation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class Shards
{
    public enum Shard
    {
        NONE(0),
        AUTONOMY(1),
        RUIN(2),
        PRESERVATION(16),
        HARMONY(17),
        HONOR(10),
        CULTIVATION(3),
        ODIUM(9),
        RETRIBUTION(18),
        ENDOWMENT(4),
        VIRTUOSITY(5),
        INVENTION(6),
        VALOR(7),
        WHIMSY(8),
        REASON(11),
        DEVOTION(12),
        DOMINION(13),
        AMBITION(14),
        MERCY(15),
        DOR(19),
        AETHER(20),
        PURE(21);

        final int id;

        Shard(int number)
        {
            this.id = number;
        }

        public static Optional<Shard> valueOf(int value)
        {
            return Arrays.stream(values())
                    .filter(shard -> shard.id == value)
                    .findFirst();
        }

        public int getID()
        {
            return id;
        }

        public String getName()
        {
            return name().toLowerCase(Locale.ROOT);
        }

        public UUID getUUID()
        {
            return UUID.fromString(name());
        }

        public Shard[] getComponentShards()
        {
            return switch (this) {
                case DOR -> new Shard[] {DEVOTION, DOMINION};
                case HARMONY -> new Shard[] {RUIN, PRESERVATION};
                case RETRIBUTION -> new Shard[] {HONOR, ODIUM};
                case PURE -> EnumUtils.SHARDS;
                default -> new Shard[] {this};

            };
        }

        public Manifestations.ManifestationTypes getManifestationType()
        {
            return switch (this) {
                case HARMONY -> Manifestations.ManifestationTypes.FERUCHEMY;
                case PRESERVATION -> Manifestations.ManifestationTypes.ALLOMANCY;
                case RUIN -> Manifestations.ManifestationTypes.HEMALURGY;
                case AUTONOMY -> Manifestations.ManifestationTypes.SANDMASTERY;
                case DOR -> Manifestations.ManifestationTypes.AON_DOR;
                case ENDOWMENT -> Manifestations.ManifestationTypes.AWAKENING;
                case HONOR, ODIUM, RETRIBUTION, CULTIVATION -> Manifestations.ManifestationTypes.SURGEBINDING;
                default -> Manifestations.ManifestationTypes.NONE;
            };
        }

        public static Shard getFromUUID(UUID uuid)
        {
            return Arrays.stream(values())
                    .filter(shard -> shard.getUUID().equals(uuid))
                    .findFirst()
                    .orElse(NONE);
        }

        public static Shard getShard(Manifestation manifestation)
        {
            return getShard(manifestation.getManifestationType());
        }

        public static Shard getShard(Manifestations.ManifestationTypes manifestationType)
        {
            return switch (manifestationType) {
                case ALLOMANCY -> PRESERVATION;
                case HEMALURGY -> RUIN;
                case SANDMASTERY -> AUTONOMY;
                case AON_DOR -> DOR;
                case AWAKENING -> ENDOWMENT;
                case SURGEBINDING -> HONOR;
                default -> NONE;
            };
        }
    }

}