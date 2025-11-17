package fpt.edu.vn.gms.resolvers;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.entity.Employee;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return (parameter.hasParameterAnnotation(CurrentUser.class) &&
        parameter.getParameterType().equals(Employee.class));
  }

  @Override
  @Nullable
  public Employee resolveArgument(
      @NonNull MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .map(Employee.class::cast)
        .orElse(null);
  }
}
