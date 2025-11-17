package fpt.edu.vn.gms.common.annotations;

import fpt.edu.vn.gms.common.enums.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify which user roles are allowed to access a particular route.
 *
 * <p>
 * By default, all {@link Role}s are permitted.
 * Apply this annotation to controller methods to restrict access based on user roles.
 *
 * For example:
 * <pre>
 * &#64;AllowRoles({Role.MANAGER})
 * public ResponseEntity<?> managerOnlyEndpoint() { ... }
 * </pre>
 *
 * @see Role
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AllowRoles {
  Role[] value() default { Role.ACCOUNTANT, Role.MANAGER, Role.SERVICE_ADVISOR, Role.WAREHOUSE };
}
