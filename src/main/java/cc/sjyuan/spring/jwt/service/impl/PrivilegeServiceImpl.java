package cc.sjyuan.spring.jwt.service.impl;

import cc.sjyuan.spring.jwt.entity.Privilege;
import cc.sjyuan.spring.jwt.repository.PrivilegeRepository;
import cc.sjyuan.spring.jwt.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;


    @Override
    @Transactional
    public void create(Privilege privilege) {
        privilegeRepository.save(privilege);
    }
}
