package cc.sjyuan.spring.jwt.service.impl;

import cc.sjyuan.spring.jwt.entity.Role;
import cc.sjyuan.spring.jwt.repository.RoleRepository;
import cc.sjyuan.spring.jwt.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void create(Role role) throws Exception {
        roleRepository.save(role);
    }
}
