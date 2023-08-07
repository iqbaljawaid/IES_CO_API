package in.globalit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.globalit.entity.RoleEntity;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {

}
