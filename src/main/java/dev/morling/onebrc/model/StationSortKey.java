package dev.morling.onebrc.model;

public class StationSortKey implements Comparable<StationSortKey>, StationHashKey {

    private final String name;

    public StationSortKey(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public int compareTo(final StationSortKey stationSortKey) {
        return name.compareTo(stationSortKey.name());
    }

    @Override
    public boolean equals(final Object object) {
        final StationSortKey stationSortKey = (StationSortKey) object;
        return name.equals(stationSortKey.name());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
