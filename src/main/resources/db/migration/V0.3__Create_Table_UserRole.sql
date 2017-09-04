CREATE TABLE `t_user_role` (
  `user_id`     VARCHAR(255) NOT NULL,
  `role_symbol` VARCHAR(255) NOT NULL,

  FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  FOREIGN KEY (`role_symbol`) REFERENCES `t_role` (`symbol`)
);