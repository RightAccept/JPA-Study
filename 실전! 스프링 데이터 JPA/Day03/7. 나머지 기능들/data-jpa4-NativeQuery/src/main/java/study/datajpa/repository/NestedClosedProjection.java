package study.datajpa.repository;

public interface NestedClosedProjection {
    // 중첩 구조 처리

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
