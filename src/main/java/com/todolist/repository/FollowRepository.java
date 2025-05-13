package com.todolist.repository;

import com.todolist.entity.Follow;
import com.todolist.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f.followee FROM Follow f WHERE f.follower = :follower")
    Page<Member> findFolloweeByFollower(Member follower, Pageable pageable);

    boolean existsByFollowerAndFollowee(Member follower, Member followee);

    void deleteByFollowerAndFollowee(Member follower, Member followee);
}
