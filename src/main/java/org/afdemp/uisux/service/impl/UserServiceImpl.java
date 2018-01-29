package org.afdemp.uisux.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.domain.security.PasswordResetToken;
import org.afdemp.uisux.domain.security.Role;
import org.afdemp.uisux.domain.security.UserRole;
import org.afdemp.uisux.repository.PasswordResetTokenRepository;
import org.afdemp.uisux.repository.RoleRepository;
import org.afdemp.uisux.repository.UserRepository;
import org.afdemp.uisux.service.RoleService;
import org.afdemp.uisux.service.UserRoleService;
import org.afdemp.uisux.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Override
	public User createUser(User user, Set<UserRole> userRoles) {
		User localUser = userRepository.findByUsername(user.getUsername());

		if (localUser != null) {
			LOG.info("\n\nFAILURE: User {} already exists. Nothing will be done.\n\n", user.getUsername());
		} 
		else 
			{
			localUser = userRepository.findByEmail(user.getEmail());
			if (localUser != null) 
				{
					LOG.info("\n\nFAILURE: User with email:{} already exists. Nothing will be done.\n\n", user.getEmail());
				}
			
			else
				{
					for (UserRole ur : userRoles) 
					{
						roleService.createRole(ur.getRole());
					}
			
					user.getUserRoles().addAll(userRoles);
										
								
					localUser = userRepository.save(user);
					
					for (UserRole ur : user.getUserRoles()) 
					{
						userRoleService.createUserRole(ur);
					}
					
					LOG.info("\n\nSUCCESS: User {} created. Database succesfully updated.\n\n", user.getUsername());
			
				}
			}

		return localUser;
	}
	
	@Override
	public void addRole(User member,String roleName)
	{
		User user = userService.findByUsername(member.getUsername());
		
		if (user != null) 
		{
			Set<UserRole> userRoles=new HashSet<UserRole>();
			userRoles.addAll(user.getUserRoles());
			Role role = new Role();
			
			
			role=roleRepository.findByName(roleName);
			
			//Not checking if the roleName could be wrong but could add
			//Left it for business logic to deal with it.
			UserRole memberRole = new UserRole(user, role);
			user.getUserRoles().add(memberRole);
			
			user.getUserRoles().removeAll(userRoles);
			
			
			
			System.out.println("\n\n\n");
			
			userService.save(user);
			
			for (UserRole ur : user.getUserRoles()) 
			{
				userRoleService.createUserRole(ur);
			}
		}
	}
	
	//Non Functional
	@Override
	public void updateUserThroughUsername(User user)
	{
		User localUser=new User();
		localUser=userRepository.findByUsername(user.getUsername());
		if(localUser!=null && !localUser.equals(user))
		{
			user.setEmail(localUser.getEmail());
			user.setUsername(localUser.getUsername());
			localUser = userRepository.save(user);
			
			user.getUserRoles().removeAll(localUser.getUserRoles());
			user.setUserRoles(user.getUserRoles());
			
			userRepository.update(user.getFirstName(),user.getLastName(),user.getPhone(),user.isEnabled(),user.getUserRoles(),user.getUsername());
			LOG.info("\n\nSUCCESS: User {} succesfully modified.\n\n", user.getUsername());
		}
	}
	
	//Non Functional
	@Override
	public void updateUserThroughEmail(String email, User user)
	{
		
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
	}
	
	@Override
	public User findByEmail (String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findOne(Long id) {
		// TODO Auto-generated method stub
		return userRepository.findOne(id);
	}

}
