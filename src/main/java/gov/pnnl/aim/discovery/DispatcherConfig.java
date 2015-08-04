/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author hamp645
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("gov.pnnl")
public class DispatcherConfig extends WebMvcConfigurerAdapter {
  /**
   * @return
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    registry.addResourceHandler("/img/**").addResourceLocations("/img/");
    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    registry.addResourceHandler("*.html*").addResourceLocations("/");
    registry.addResourceHandler("*.json*").addResourceLocations("/");
    registry.addResourceHandler("*.tsv*").addResourceLocations("/");
    registry.addResourceHandler("/lib/**").addResourceLocations("/lib/");
    registry.addResourceHandler("/lob/**").addResourceLocations("/lob/");
    registry.addResourceHandler("/bio/**").addResourceLocations("/bio/");
    registry.addResourceHandler("/biz/**").addResourceLocations("/biz/");
  }
}
