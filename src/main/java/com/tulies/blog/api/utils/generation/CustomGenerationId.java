package com.tulies.blog.api.utils.generation;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 21:15 2022/02/24
 */

/**
 * Created with IntelliJ IDEA.
 * User: liangqing.zhao(zlq)
 * Date: 2019/10/4 19:01
 * Description:
 */
public class CustomGenerationId implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        SnowFlake snowFlake = new SnowFlake(1, 1);
        return String.valueOf(snowFlake.nextId());
    }
}