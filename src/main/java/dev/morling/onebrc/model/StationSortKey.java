package dev.morling.onebrc.model;

public class StationSortKey implements Comparable<StationSortKey>, StationKey {

    private String name;

    public StationSortKey() {
    }

    public StationSortKey(final StationSortKey stationSortKey) {
        this(stationSortKey.name);
    }

    public StationSortKey(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void name(final String name) {
        this.name = name;
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

    @Override
    public StationKey duplicate() {
        return new StationSortKey(this);
    }
}
