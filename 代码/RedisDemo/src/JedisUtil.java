  
  
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;  
  
/** 
 * @author Mr.hu 
 * @version crateTime��2013-10-30 ����5:41:30 
 * Class Explain:JedisUtil   
 */  
public class JedisUtil {   
      
     private Logger log = Logger.getLogger(this.getClass());    
     /**��������ʱ�� */  
     private final int expire = 60000;  
     /** ����Key�ķ��� */  
     public Keys KEYS;  
     /** �Դ洢�ṹΪString���͵Ĳ��� */  
     public Strings STRINGS;  
     /** �Դ洢�ṹΪList���͵Ĳ��� */  
     public Lists LISTS;  
     /** �Դ洢�ṹΪSet���͵Ĳ��� */  
     public Sets SETS;  
     /** �Դ洢�ṹΪHashMap���͵Ĳ��� */  
     public Hash HASH;  
     /** �Դ洢�ṹΪSet(�����)���͵Ĳ��� */  
     public SortSet SORTSET;  
     private static JedisPool jedisPool = null;    
           
     private JedisUtil() {     
          
     }   
     static {    
            JedisPoolConfig config = new JedisPoolConfig();  
            config.setMaxActive(1024);     
            config.setMaxIdle(200);  
            config.setMaxWait((long)1000);
            config.setTestOnBorrow(true);  
            config.setTestOnReturn(true);   
            //redis������������룺  
           /* //jedisPool = new JedisPool(config, "127.0.0.1",   
                    JRedisPoolConfig.REDIS_PORT,  
                    10000,JRedisPoolConfig.REDIS_PASSWORD); */     
            //redisδ���������룺  
          jedisPool = new JedisPool(config, "127.0.0.1",6379);   
       }  
       
    public JedisPool getPool() {    
        return jedisPool;   
    }  
      
     /** 
      * ��jedis���ӳ��л�ȡ��ȡjedis����   
      * @return 
      */  
     public Jedis getJedis() {    
         return jedisPool.getResource();   
    }  
       
       
     private static final JedisUtil jedisUtil = new JedisUtil();  
       
   
    /** 
     * ��ȡJedisUtilʵ�� 
     * @return 
     */  
    public static JedisUtil getInstance() {  
        return jedisUtil;   
    }  
  
    /** 
     * ����jedis 
     * @param jedis 
     */  
    public void returnJedis(Jedis jedis) {  
        jedisPool.returnResource(jedis);  
    }   
  
      
    /** 
     * ���ù���ʱ�� 
     * @author ruan 2013-4-11 
     * @param key 
     * @param seconds 
     */  
    public void expire(String key, int seconds) {  
        if (seconds <= 0) {   
            return;  
        }  
        Jedis jedis = getJedis();  
        jedis.expire(key, seconds);  
        returnJedis(jedis);  
    }  
  
    /** 
     * ����Ĭ�Ϲ���ʱ�� 
     * @author ruan 2013-4-11 
     * @param key 
     */  
    public void expire(String key) {  
        expire(key, expire);  
    }  
      
      
    //*******************************************Keys*******************************************//  
    public class Keys {  
  
        /** 
         * �������key 
         */  
        public String flushAll() {  
            Jedis jedis = getJedis();  
            String stata = jedis.flushAll();  
            returnJedis(jedis);  
            return stata;  
        }  
  
        /** 
         * ����key 
         * @param String oldkey 
         * @param String  newkey 
         * @return ״̬�� 
         * */  
        public String rename(String oldkey, String newkey) {   
            return rename(SafeEncoder.encode(oldkey),  
                    SafeEncoder.encode(newkey));  
        }  
  
        /** 
         * ����key,������key������ʱ��ִ�� 
         * @param String oldkey 
         * @param String newkey  
         * @return ״̬�� 
         * */  
        public long renamenx(String oldkey, String newkey) {  
            Jedis jedis = getJedis();  
            long status = jedis.renamenx(oldkey, newkey);  
            returnJedis(jedis);  
            return status;  
        }  
  
        /** 
         * ����key 
         * @param String oldkey 
         * @param String newkey 
         * @return ״̬�� 
         * */  
        public String rename(byte[] oldkey, byte[] newkey) {  
            Jedis jedis = getJedis();  
            String status = jedis.rename(oldkey, newkey);  
            returnJedis(jedis);  
            return status;  
        }  
  
        /** 
         * ����key�Ĺ���ʱ�䣬����Ϊ��λ 
         * @param String key 
         * @param ʱ��,����Ϊ��λ 
         * @return Ӱ��ļ�¼�� 
         * */  
        public long expired(String key, int seconds) {  
            Jedis jedis = getJedis();  
            long count = jedis.expire(key, seconds);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ����key�Ĺ���ʱ��,���Ǿ���Ԫ�����������α�׼ʱ�� 1970 �� 1 �� 1 �յ� 00:00:00���������������ƫ������ 
         * @param String key 
         * @param ʱ��,����Ϊ��λ 
         * @return Ӱ��ļ�¼�� 
         * */  
        public long expireAt(String key, long timestamp) {  
            Jedis jedis = getJedis();  
            long count = jedis.expireAt(key, timestamp);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ��ѯkey�Ĺ���ʱ�� 
         * @param String key 
         * @return ����Ϊ��λ��ʱ���ʾ 
         * */  
        public long ttl(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis=getJedis();   
            long len = sjedis.ttl(key);  
            returnJedis(sjedis);  
            return len;  
        }  
  
        /** 
         * ȡ����key����ʱ������� 
         * @param key 
         * @return Ӱ��ļ�¼�� 
         * */  
        public long persist(String key) {  
            Jedis jedis = getJedis();  
            long count = jedis.persist(key);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ɾ��keys��Ӧ�ļ�¼,�����Ƕ��key 
         * @param String  ... keys 
         * @return ɾ���ļ�¼�� 
         * */  
        public long del(String... keys) {  
            Jedis jedis = getJedis();  
            long count = jedis.del(keys);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ɾ��keys��Ӧ�ļ�¼,�����Ƕ��key 
         * @param String .. keys 
         * @return ɾ���ļ�¼�� 
         * */  
        public long del(byte[]... keys) {  
            Jedis jedis = getJedis();  
            long count = jedis.del(keys);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * �ж�key�Ƿ���� 
         * @param String key 
         * @return boolean 
         * */  
        public boolean exists(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis=getJedis();    
            boolean exis = sjedis.exists(key);  
            returnJedis(sjedis);  
            return exis;  
        }  
  
        /** 
         * ��List,Set,SortSet��������,����������ݽϴ�Ӧ����ʹ��������� 
         * @param String key 
         * @return List<String> ���ϵ�ȫ����¼ 
         * **/  
        public List<String> sort(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis=getJedis();    
            List<String> list = sjedis.sort(key);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ��List,Set,SortSet���������limit 
         * @param String key 
         * @param SortingParams parame �����������ͻ�limit����ֹλ��. 
         * @return List<String> ȫ���򲿷ּ�¼ 
         * **/  
        public List<String> sort(String key, SortingParams parame) {  
            //ShardedJedis sjedis = getShardedJedis();   
            Jedis sjedis=getJedis();   
            List<String> list = sjedis.sort(key, parame);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ����ָ��key�洢������ 
         * @param String key 
         * @return String string|list|set|zset|hash 
         * **/  
        public String type(String key) {  
            //ShardedJedis sjedis = getShardedJedis();   
            Jedis sjedis=getJedis();    
            String type = sjedis.type(key);   
            returnJedis(sjedis);  
            return type;  
        }  
  
        /** 
         * ��������ƥ�������ģʽ�ļ� 
         * @param String  key�ı��ʽ,*��ʾ���������ʾһ�� 
         * */  
        public Set<String> keys(String pattern) {  
            Jedis jedis = getJedis();  
            Set<String> set = jedis.keys(pattern);  
            returnJedis(jedis);  
            return set;  
        }  
    }  
  
    //*******************************************Sets*******************************************//  
    public class Sets {  
  
        /** 
         * ��Set���һ����¼�����member�Ѵ��ڷ���0,���򷵻�1 
         * @param String  key 
         * @param String member 
         * @return ������,0��1 
         * */  
        public long sadd(String key, String member) {  
            Jedis jedis = getJedis();  
            long s = jedis.sadd(key, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        public long sadd(byte[] key, byte[] member) {  
            Jedis jedis = getJedis();  
            long s = jedis.sadd(key, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��ȡ����key��Ԫ�ظ��� 
         * @param String key 
         * @return Ԫ�ظ��� 
         * */  
        public long scard(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            long len = sjedis.scard(key);  
            returnJedis(sjedis);  
            return len;  
        }  
  
        /** 
         * ���شӵ�һ������еĸ�������֮��Ĳ���ĳ�Ա 
         * @param String ... keys 
         * @return ����ĳ�Ա���� 
         * */  
        public Set<String> sdiff(String... keys) {  
            Jedis jedis = getJedis();  
            Set<String> set = jedis.sdiff(keys);  
            returnJedis(jedis);  
            return set;  
        }  
  
        /** 
         * ����������sdiff,�����صĲ��ǽ����,���ǽ�������洢���µļ����У����Ŀ���Ѵ��ڣ��򸲸ǡ� 
         * @param String newkey �½������key 
         * @param String ... keys �Ƚϵļ��� 
         * @return �¼����еļ�¼�� 
         * **/  
        public long sdiffstore(String newkey, String... keys) {  
            Jedis jedis = getJedis();  
            long s = jedis.sdiffstore(newkey, keys);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ���ظ������Ͻ����ĳ�Ա,�������һ������Ϊ�����ڻ�Ϊ�գ��򷵻ؿ�Set 
         * @param String ... keys 
         * @return ������Ա�ļ��� 
         * **/  
        public Set<String> sinter(String... keys) {  
            Jedis jedis = getJedis();  
            Set<String> set = jedis.sinter(keys);  
            returnJedis(jedis);  
            return set;  
        }  
  
        /** 
         * ����������sinter,�����صĲ��ǽ����,���ǽ�������洢���µļ����У����Ŀ���Ѵ��ڣ��򸲸ǡ� 
         * @param String  newkey �½������key 
         * @param String ... keys �Ƚϵļ��� 
         * @return �¼����еļ�¼�� 
         * **/  
        public long sinterstore(String newkey, String... keys) {  
            Jedis jedis = getJedis();  
            long s = jedis.sinterstore(newkey, keys);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ȷ��һ��������ֵ�Ƿ���� 
         * @param String  key 
         * @param String member Ҫ�жϵ�ֵ 
         * @return ���ڷ���1�������ڷ���0 
         * **/  
        public boolean sismember(String key, String member) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            boolean s = sjedis.sismember(key, member);  
            returnJedis(sjedis);  
            return s;  
        }  
  
        /** 
         * ���ؼ����е����г�Ա 
         * @param String  key 
         * @return ��Ա���� 
         * */  
        public Set<String> smembers(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Set<String> set = sjedis.smembers(key);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        public Set<byte[]> smembers(byte[] key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            Set<byte[]> set = sjedis.smembers(key);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        /** 
         * ����Ա��Դ�����Ƴ�����Ŀ�꼯�� <br/> 
         * ���Դ���ϲ����ڻ򲻰���ָ����Ա���������κβ���������0<br/> 
         * ����ó�Ա��Դ������ɾ��������ӵ�Ŀ�꼯�ϣ����Ŀ�꼯���г�Ա�Ѵ��ڣ���ֻ��Դ���Ͻ���ɾ�� 
         * @param String  srckey Դ���� 
         * @param String dstkey Ŀ�꼯�� 
         * @param String member Դ�����еĳ�Ա 
         * @return ״̬�룬1�ɹ���0ʧ�� 
         * */  
        public long smove(String srckey, String dstkey, String member) {  
            Jedis jedis = getJedis();  
            long s = jedis.smove(srckey, dstkey, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * �Ӽ�����ɾ����Ա 
         * @param String  key 
         * @return ��ɾ���ĳ�Ա 
         * */  
        public String spop(String key) {  
            Jedis jedis = getJedis();  
            String s = jedis.spop(key);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * �Ӽ�����ɾ��ָ����Ա 
         * @param String key 
         * @param String  member Ҫɾ���ĳ�Ա 
         * @return ״̬�룬�ɹ�����1����Ա�����ڷ���0 
         * */  
        public long srem(String key, String member) {  
            Jedis jedis = getJedis();  
            long s = jedis.srem(key, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * �ϲ�������ϲ����غϲ���Ľ�����ϲ���Ľ�����ϲ�������<br/> 
         * @param String  ... keys 
         * @return �ϲ���Ľ������ 
         * @see sunionstore 
         * */  
        public Set<String> sunion(String... keys) {  
            Jedis jedis = getJedis();  
            Set<String> set = jedis.sunion(keys);  
            returnJedis(jedis);  
            return set;  
        }  
  
        /** 
         * �ϲ�������ϲ����ϲ���Ľ����������ָ�����¼����У�����¼����Ѿ������򸲸� 
         * @param String  newkey �¼��ϵ�key 
         * @param String ... keys Ҫ�ϲ��ļ��� 
         * **/  
        public long sunionstore(String newkey, String... keys) {  
            Jedis jedis = getJedis();  
            long s = jedis.sunionstore(newkey, keys);  
            returnJedis(jedis);  
            return s;  
        }  
    }  
  
    //*******************************************SortSet*******************************************//  
    public class SortSet {  
  
        /** 
         * �򼯺�������һ����¼,������ֵ�Ѵ��ڣ����ֵ��Ӧ��Ȩ�ؽ�����Ϊ�µ�Ȩ�� 
         * @param String  key 
         * @param double score Ȩ�� 
         * @param String  member Ҫ�����ֵ�� 
         * @return ״̬�� 1�ɹ���0�Ѵ���member��ֵ 
         * */  
        public long zadd(String key, double score, String member) {  
            Jedis jedis = getJedis();  
            long s = jedis.zadd(key, score, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        public long zadd(String key, Map<Double, String> scoreMembers) {  
            Jedis jedis = getJedis();  
            long s = jedis.zadd(key, scoreMembers);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��ȡ������Ԫ�ص����� 
         * @param String  key 
         * @return �������0�򼯺ϲ����� 
         * */  
        public long zcard(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();  
            long len = sjedis.zcard(key);  
            returnJedis(sjedis);  
            return len;  
        }  
  
        /** 
         * ��ȡָ��Ȩ�������ڼ��ϵ����� 
         * @param String key 
         * @param double min ��С����λ�� 
         * @param double max �������λ�� 
         * */  
        public long zcount(String key, double min, double max) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();  
            long len = sjedis.zcount(key, min, max);  
            returnJedis(sjedis);  
            return len;  
        }  
  
        /** 
         * ���set�ĳ��� 
         *  
         * @param key 
         * @return 
         */  
        public long zlength(String key) {  
            long len = 0;  
            Set<String> set = zrange(key, 0, -1);  
            len = set.size();  
            return len;  
        }  
  
        /** 
         * Ȩ�����Ӹ���ֵ�����������member�Ѵ��� 
         * @param String  key 
         * @param double score Ҫ����Ȩ�� 
         * @param String  member Ҫ�����ֵ 
         * @return �����Ȩ�� 
         * */  
        public double zincrby(String key, double score, String member) {  
            Jedis jedis = getJedis();  
            double s = jedis.zincrby(key, score, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ����ָ��λ�õļ���Ԫ��,0Ϊ��һ��Ԫ�أ�-1Ϊ���һ��Ԫ�� 
         * @param String key 
         * @param int start ��ʼλ��(����) 
         * @param int end ����λ��(����) 
         * @return Set<String> 
         * */  
        public Set<String> zrange(String key, int start, int end) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Set<String> set = sjedis.zrange(key, start, end);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        /** 
         * ����ָ��Ȩ�������Ԫ�ؼ��� 
         * @param String key 
         * @param double min ����Ȩ�� 
         * @param double max ����Ȩ�� 
         * @return Set<String> 
         * */  
        public Set<String> zrangeByScore(String key, double min, double max) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Set<String> set = sjedis.zrangeByScore(key, min, max);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        /** 
         * ��ȡָ��ֵ�ڼ����е�λ�ã���������ӵ͵��� 
         * @see zrevrank 
         * @param String key 
         * @param String member 
         * @return long λ�� 
         * */  
        public long zrank(String key, String member) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            long index = sjedis.zrank(key, member);  
            returnJedis(sjedis);  
            return index;  
        }  
  
        /** 
         * ��ȡָ��ֵ�ڼ����е�λ�ã���������Ӹߵ��� 
         * @see zrank 
         * @param String key 
         * @param String member 
         * @return long λ�� 
         * */  
        public long zrevrank(String key, String member) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            long index = sjedis.zrevrank(key, member);  
            returnJedis(sjedis);  
            return index;  
        }  
  
        /** 
         * �Ӽ�����ɾ����Ա 
         * @param String key 
         * @param String member  
         * @return ����1�ɹ� 
         * */  
        public long zrem(String key, String member) {  
            Jedis jedis = getJedis();  
            long s = jedis.zrem(key, member);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ɾ�� 
         * @param key 
         * @return 
         */  
        public long zrem(String key) {  
            Jedis jedis = getJedis();  
            long s = jedis.del(key);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ɾ������λ�������Ԫ�� 
         * @param String  key 
         * @param int start ��ʼ���䣬��0��ʼ(����) 
         * @param int end ��������,-1Ϊ���һ��Ԫ��(����) 
         * @return ɾ�������� 
         * */  
        public long zremrangeByRank(String key, int start, int end) {  
            Jedis jedis = getJedis();  
            long s = jedis.zremrangeByRank(key, start, end);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ɾ������Ȩ�������Ԫ�� 
         * @param String key 
         * @param double min ����Ȩ��(����) 
         * @param double max ����Ȩ��(����) 
         * @return ɾ�������� 
         * */  
        public long zremrangeByScore(String key, double min, double max) {  
            Jedis jedis = getJedis();  
            long s = jedis.zremrangeByScore(key, min, max);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��ȡ���������Ԫ�أ�ԭʼ����Ȩ���ɸߵ������� 
         * @param String  key 
         * @param int start 
         * @param int end 
         * @return Set<String> 
         * */  
        public Set<String> zrevrange(String key, int start, int end) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Set<String> set = sjedis.zrevrange(key, start, end);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        /** 
         * ��ȡ����ֵ�ڼ����е�Ȩ�� 
         * @param String  key 
         * @param memeber 
         * @return double Ȩ�� 
         * */  
        public double zscore(String key, String memebr) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Double score = sjedis.zscore(key, memebr);  
            returnJedis(sjedis);  
            if (score != null)  
                return score;  
            return 0;  
        }  
    }  
      
    //*******************************************Hash*******************************************//  
    public class Hash {  
  
        /** 
         * ��hash��ɾ��ָ���Ĵ洢 
         * @param String key 
         * @param String  fieid �洢������ 
         * @return ״̬�룬1�ɹ���0ʧ�� 
         * */  
        public long hdel(String key, String fieid) {  
            Jedis jedis = getJedis();  
            long s = jedis.hdel(key, fieid);  
            returnJedis(jedis);  
            return s;  
        }  
  
        public long hdel(String key) {  
            Jedis jedis = getJedis();  
            long s = jedis.del(key);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ����hash��ָ���Ĵ洢�Ƿ���� 
         * @param String key 
         * @param String  fieid �洢������ 
         * @return 1���ڣ�0������ 
         * */  
        public boolean hexists(String key, String fieid) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            boolean s = sjedis.hexists(key, fieid);  
            returnJedis(sjedis);  
            return s;  
        }  
  
        /** 
         * ����hash��ָ���洢λ�õ�ֵ 
         *  
         * @param String key 
         * @param String fieid �洢������ 
         * @return �洢��Ӧ��ֵ 
         * */  
        public String hget(String key, String fieid) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            String s = sjedis.hget(key, fieid);  
            returnJedis(sjedis);  
            return s;  
        }  
  
        public byte[] hget(byte[] key, byte[] fieid) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            byte[] s = sjedis.hget(key, fieid);  
            returnJedis(sjedis);  
            return s;  
        }  
  
        /** 
         * ��Map����ʽ����hash�еĴ洢��ֵ 
         * @param String    key 
         * @return Map<Strinig,String> 
         * */  
        public Map<String, String> hgetAll(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Map<String, String> map = sjedis.hgetAll(key);  
            returnJedis(sjedis);  
            return map;  
        }  
  
        /** 
         * ���һ����Ӧ��ϵ 
         * @param String  key 
         * @param String fieid 
         * @param String value 
         * @return ״̬�� 1�ɹ���0ʧ�ܣ�fieid�Ѵ��ڽ����£�Ҳ����0 
         * **/  
        public long hset(String key, String fieid, String value) {  
            Jedis jedis = getJedis();  
            long s = jedis.hset(key, fieid, value);  
            returnJedis(jedis);  
            return s;  
        }  
  
        public long hset(String key, String fieid, byte[] value) {  
            Jedis jedis = getJedis();  
            long s = jedis.hset(key.getBytes(), fieid.getBytes(), value);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��Ӷ�Ӧ��ϵ��ֻ����fieid������ʱ��ִ�� 
         * @param String key 
         * @param String fieid 
         * @param String value 
         * @return ״̬�� 1�ɹ���0ʧ��fieid�Ѵ� 
         * **/  
        public long hsetnx(String key, String fieid, String value) {  
            Jedis jedis = getJedis();  
            long s = jedis.hsetnx(key, fieid, value);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��ȡhash��value�ļ��� 
         *  
         * @param String 
         *            key 
         * @return List<String> 
         * */  
        public List<String> hvals(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            List<String> list = sjedis.hvals(key);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ��ָ���Ĵ洢λ�ü���ָ�������֣��洢λ�õ�ֵ�����תΪ�������� 
         * @param String  key 
         * @param String  fieid �洢λ�� 
         * @param String long value Ҫ���ӵ�ֵ,�����Ǹ��� 
         * @return ����ָ�����ֺ󣬴洢λ�õ�ֵ 
         * */  
        public long hincrby(String key, String fieid, long value) {  
            Jedis jedis = getJedis();  
            long s = jedis.hincrBy(key, fieid, value);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ����ָ��hash�е����д洢����,����Map�е�keySet���� 
         * @param String key 
         * @return Set<String> �洢���Ƶļ��� 
         * */  
        public Set<String> hkeys(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            Set<String> set = sjedis.hkeys(key);  
            returnJedis(sjedis);  
            return set;  
        }  
  
        /** 
         * ��ȡhash�д洢�ĸ���������Map��size���� 
         * @param String  key 
         * @return long �洢�ĸ��� 
         * */  
        public long hlen(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            long len = sjedis.hlen(key);  
            returnJedis(sjedis);  
            return len;  
        }  
  
        /** 
         * ���ݶ��key����ȡ��Ӧ��value������List,���ָ����key������,List��Ӧλ��Ϊnull 
         * @param String  key 
         * @param String ... fieids �洢λ�� 
         * @return List<String> 
         * */  
        public List<String> hmget(String key, String... fieids) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();   
            List<String> list = sjedis.hmget(key, fieids);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        public List<byte[]> hmget(byte[] key, byte[]... fieids) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            List<byte[]> list = sjedis.hmget(key, fieids);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ��Ӷ�Ӧ��ϵ�������Ӧ��ϵ�Ѵ��ڣ��򸲸� 
         * @param Strin   key 
         * @param Map <String,String> ��Ӧ��ϵ 
         * @return ״̬���ɹ�����OK 
         * */  
        public String hmset(String key, Map<String, String> map) {  
            Jedis jedis = getJedis();  
            String s = jedis.hmset(key, map);  
            returnJedis(jedis);  
            return s;  
        }  
  
        /** 
         * ��Ӷ�Ӧ��ϵ�������Ӧ��ϵ�Ѵ��ڣ��򸲸� 
         * @param Strin key 
         * @param Map <String,String> ��Ӧ��ϵ 
         * @return ״̬���ɹ�����OK 
         * */  
        public String hmset(byte[] key, Map<byte[], byte[]> map) {  
            Jedis jedis = getJedis();  
            String s = jedis.hmset(key, map);  
            returnJedis(jedis);  
            return s;  
        }  
  
    }  
      
      
    //*******************************************Strings*******************************************//  
    public class Strings {  
        /** 
         * ����key��ȡ��¼ 
         * @param String  key 
         * @return ֵ 
         * */  
        public String get(String key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            String value = sjedis.get(key);  
            returnJedis(sjedis);  
            return value;  
        }  
  
        /** 
         * ����key��ȡ��¼ 
         * @param byte[] key 
         * @return ֵ 
         * */  
        public byte[] get(byte[] key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            byte[] value = sjedis.get(key);  
            returnJedis(sjedis);  
            return value;  
        }  
  
        /** 
         * ����й���ʱ��ļ�¼ 
         *  
         * @param String  key 
         * @param int seconds ����ʱ�䣬����Ϊ��λ 
         * @param String value 
         * @return String ����״̬ 
         * */  
        public String setEx(String key, int seconds, String value) {  
            Jedis jedis = getJedis();  
            String str = jedis.setex(key, seconds, value);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * ����й���ʱ��ļ�¼ 
         *  
         * @param String key 
         * @param int seconds ����ʱ�䣬����Ϊ��λ 
         * @param String  value 
         * @return String ����״̬ 
         * */  
        public String setEx(byte[] key, int seconds, byte[] value) {  
            Jedis jedis = getJedis();  
            String str = jedis.setex(key, seconds, value);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * ���һ����¼������������key������ʱ�Ų��� 
         * @param String key 
         * @param String value 
         * @return long ״̬�룬1����ɹ���key�����ڣ�0δ���룬key���� 
         * */  
        public long setnx(String key, String value) {  
            Jedis jedis = getJedis();  
            long str = jedis.setnx(key, value);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * ��Ӽ�¼,�����¼�Ѵ��ڽ�����ԭ�е�value 
         * @param String key 
         * @param String value 
         * @return ״̬�� 
         * */  
        public String set(String key, String value) {  
            return set(SafeEncoder.encode(key), SafeEncoder.encode(value));  
        }  
  
        /** 
         * ��Ӽ�¼,�����¼�Ѵ��ڽ�����ԭ�е�value 
         * @param String  key 
         * @param String value 
         * @return ״̬�� 
         * */  
        public String set(String key, byte[] value) {  
            return set(SafeEncoder.encode(key), value);  
        }  
  
        /** 
         * ��Ӽ�¼,�����¼�Ѵ��ڽ�����ԭ�е�value 
         * @param byte[] key 
         * @param byte[] value 
         * @return ״̬�� 
         * */  
        public String set(byte[] key, byte[] value) {  
            Jedis jedis = getJedis();  
            String status = jedis.set(key, value);  
            returnJedis(jedis);  
            return status;  
        }  
  
        /** 
         * ��ָ��λ�ÿ�ʼ�������ݣ���������ݻḲ��ָ��λ���Ժ������<br/> 
         * ��:String str1="123456789";<br/> 
         * ��str1������setRange(key,4,0000)��str1="123400009"; 
         * @param String  key 
         * @param long offset 
         * @param String  value 
         * @return long value�ĳ��� 
         * */  
        public long setRange(String key, long offset, String value) {  
            Jedis jedis = getJedis();  
            long len = jedis.setrange(key, offset, value);  
            returnJedis(jedis);  
            return len;  
        }  
  
        /** 
         * ��ָ����key��׷��value 
         * @param String  key 
         * @param String value 
         * @return long ׷�Ӻ�value�ĳ��� 
         * **/  
        public long append(String key, String value) {  
            Jedis jedis = getJedis();  
            long len = jedis.append(key, value);  
            returnJedis(jedis);  
            return len;  
        }  
  
        /** 
         * ��key��Ӧ��value��ȥָ����ֵ��ֻ��value����תΪ����ʱ�÷����ſ��� 
         * @param String key 
         * @param long number Ҫ��ȥ��ֵ 
         * @return long ��ָ��ֵ���ֵ 
         * */  
        public long decrBy(String key, long number) {  
            Jedis jedis = getJedis();  
            long len = jedis.decrBy(key, number);  
            returnJedis(jedis);  
            return len;  
        }  
  
        /** 
         * <b>������Ϊ��ȡΨһid�ķ���</b><br/> 
         * ��key��Ӧ��value����ָ����ֵ��ֻ��value����תΪ����ʱ�÷����ſ��� 
         * @param String  key 
         * @param long number Ҫ��ȥ��ֵ 
         * @return long ��Ӻ��ֵ 
         * */  
        public long incrBy(String key, long number) {  
            Jedis jedis = getJedis();  
            long len = jedis.incrBy(key, number);  
            returnJedis(jedis);  
            return len;  
        }  
  
        /** 
         * ��ָ��key��Ӧ��value���н�ȡ  
         * @param String   key 
         * @param long startOffset ��ʼλ��(����) 
         * @param long endOffset ����λ��(����) 
         * @return String ��ȡ��ֵ 
         * */  
        public String getrange(String key, long startOffset, long endOffset) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            String value = sjedis.getrange(key, startOffset, endOffset);  
            returnJedis(sjedis);   
            return value;  
        }  
  
        /** 
         * ��ȡ������ָ��key��Ӧ��value<br/> 
         * ���key���ڷ���֮ǰ��value,���򷵻�null 
         * @param String  key 
         * @param String value 
         * @return String ԭʼvalue��null 
         * */  
        public String getSet(String key, String value) {  
            Jedis jedis = getJedis();  
            String str = jedis.getSet(key, value);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * ������ȡ��¼,���ָ����key�����ڷ���List�Ķ�Ӧλ�ý���null 
         * @param String keys 
         * @return List<String> ֵ�ü��� 
         * */  
        public List<String> mget(String... keys) {  
            Jedis jedis = getJedis();  
            List<String> str = jedis.mget(keys);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * �����洢��¼ 
         * @param String keysvalues ��:keysvalues="key1","value1","key2","value2"; 
         * @return String ״̬��  
         * */  
        public String mset(String... keysvalues) {  
            Jedis jedis = getJedis();  
            String str = jedis.mset(keysvalues);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /** 
         * ��ȡkey��Ӧ��ֵ�ĳ��� 
         * @param String key 
         * @return valueֵ�ó��� 
         * */  
        public long strlen(String key) {  
            Jedis jedis = getJedis();  
            long len = jedis.strlen(key);  
            returnJedis(jedis);  
            return len;  
        }  
    }  
      
      
    //*******************************************Lists*******************************************//  
    public class Lists {  
        /** 
         * List���� 
         * @param String key 
         * @return ���� 
         * */  
        public long llen(String key) {  
            return llen(SafeEncoder.encode(key));  
        }  
  
        /** 
         * List���� 
         * @param byte[] key 
         * @return ���� 
         * */  
        public long llen(byte[] key) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            long count = sjedis.llen(key);  
            returnJedis(sjedis);  
            return count;  
        }  
  
        /** 
         * ���ǲ���,������List��ָ��λ�õ�ֵ 
         * @param byte[] key 
         * @param int index λ�� 
         * @param byte[] value ֵ 
         * @return ״̬�� 
         * */  
        public String lset(byte[] key, int index, byte[] value) {  
            Jedis jedis = getJedis();  
            String status = jedis.lset(key, index, value);  
            returnJedis(jedis);  
            return status;  
        }  
  
        /** 
         * ���ǲ���,������List��ָ��λ�õ�ֵ 
         * @param key 
         * @param int index λ�� 
         * @param String  value ֵ 
         * @return ״̬�� 
         * */  
        public String lset(String key, int index, String value) {  
            return lset(SafeEncoder.encode(key), index,  
                    SafeEncoder.encode(value));  
        }  
  
        /** 
         * ��value�����λ�ò����¼ 
         * @param key 
         * @param LIST_POSITION   ǰ������������ 
         * @param String pivot ���λ�õ����� 
         * @param String value ��������� 
         * @return ��¼���� 
         * */  
        public long linsert(String key, LIST_POSITION where, String pivot,  
                String value) {  
            return linsert(SafeEncoder.encode(key), where,  
                    SafeEncoder.encode(pivot), SafeEncoder.encode(value));  
        }  
  
        /** 
         * ��ָ��λ�ò����¼ 
         * @param String key 
         * @param LIST_POSITION ǰ������������ 
         * @param byte[] pivot ���λ�õ����� 
         * @param byte[] value ��������� 
         * @return ��¼���� 
         * */  
        public long linsert(byte[] key, LIST_POSITION where, byte[] pivot,  
                byte[] value) {  
            Jedis jedis = getJedis();  
            long count = jedis.linsert(key, where, pivot, value);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ��ȡList��ָ��λ�õ�ֵ 
         * @param String  key 
         * @param int index λ��  
         * @return ֵ 
         * **/  
        public String lindex(String key, int index) {  
            return SafeEncoder.encode(lindex(SafeEncoder.encode(key), index));  
        }  
  
        /** 
         * ��ȡList��ָ��λ�õ�ֵ  
         * @param byte[] key 
         * @param int index λ�� 
         * @return ֵ 
         * **/  
        public byte[] lindex(byte[] key, int index) {   
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();    
            byte[] value = sjedis.lindex(key, index);  
            returnJedis(sjedis);  
            return value;  
        }  
  
        /** 
         * ��List�еĵ�һ����¼�Ƴ�List 
         * @param String key 
         * @return �Ƴ��ļ�¼  
         * */  
        public String lpop(String key) {  
            return SafeEncoder.encode(lpop(SafeEncoder.encode(key)));  
        }  
  
        /** 
         * ��List�еĵ�һ����¼�Ƴ�List 
         * @param byte[] key 
         * @return �Ƴ��ļ�¼ 
         * */  
        public byte[] lpop(byte[] key) {  
            Jedis jedis = getJedis();  
            byte[] value = jedis.lpop(key);  
            returnJedis(jedis);  
            return value;  
        }  
  
        /** 
         * ��List������һ����¼�Ƴ�List 
         *  
         * @param byte[] key 
         * @return �Ƴ��ļ�¼ 
         * */  
        public String rpop(String key) {  
            Jedis jedis = getJedis();  
            String value = jedis.rpop(key);  
            returnJedis(jedis);  
            return value;  
        }  
  
        /** 
         * ��Listβ��׷�Ӽ�¼ 
         * @param String key 
         * @param String value 
         * @return ��¼���� 
         * */  
        public long lpush(String key, String value) {  
            return lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));  
        }  
  
        /** 
         * ��Listͷ��׷�Ӽ�¼ 
         * @param String  key 
         * @param String  value 
         * @return ��¼���� 
         * */  
        public long rpush(String key, String value) {  
            Jedis jedis = getJedis();  
            long count = jedis.rpush(key, value);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ��Listͷ��׷�Ӽ�¼ 
         * @param String key 
         * @param String value 
         * @return ��¼���� 
         * */  
        public long rpush(byte[] key, byte[] value) {  
            Jedis jedis = getJedis();  
            long count = jedis.rpush(key, value);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ��List��׷�Ӽ�¼ 
         * @param byte[] key 
         * @param byte[] value 
         * @return ��¼���� 
         * */  
        public long lpush(byte[] key, byte[] value) {  
            Jedis jedis = getJedis();  
            long count = jedis.lpush(key, value);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ��ȡָ����Χ�ļ�¼��������Ϊ��ҳʹ�� 
         * @param String key 
         * @param long start 
         * @param long end 
         * @return List 
         * */  
        public List<String> lrange(String key, long start, long end) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();     
            List<String> list = sjedis.lrange(key, start, end);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ��ȡָ����Χ�ļ�¼��������Ϊ��ҳʹ�� 
         * @param byte[] key 
         * @param int start 
         * @param int end ���Ϊ��������β����ʼ���� 
         * @return List 
         * */  
        public List<byte[]> lrange(byte[] key, int start, int end) {  
            //ShardedJedis sjedis = getShardedJedis();  
            Jedis sjedis = getJedis();     
            List<byte[]> list = sjedis.lrange(key, start, end);  
            returnJedis(sjedis);  
            return list;  
        }  
  
        /** 
         * ɾ��List��c����¼����ɾ���ļ�¼ֵΪvalue 
         * @param byte[] key 
         * @param int c Ҫɾ�������������Ϊ�������List��β����鲢ɾ�����ϵļ�¼ 
         * @param byte[] value Ҫƥ���ֵ 
         * @return ɾ�����List�еļ�¼�� 
         * */  
        public long lrem(byte[] key, int c, byte[] value) {  
            Jedis jedis = getJedis();  
            long count = jedis.lrem(key, c, value);  
            returnJedis(jedis);  
            return count;  
        }  
  
        /** 
         * ɾ��List��c����¼����ɾ���ļ�¼ֵΪvalue 
         * @param String key 
         * @param int c Ҫɾ�������������Ϊ�������List��β����鲢ɾ�����ϵļ�¼ 
         * @param String value Ҫƥ���ֵ 
         * @return ɾ�����List�еļ�¼�� 
         * */  
        public long lrem(String key, int c, String value) {  
            return lrem(SafeEncoder.encode(key), c, SafeEncoder.encode(value));  
        }  
  
        /** 
         * ����ɾ���ɣ�ֻ����start��end֮��ļ�¼ 
         * @param byte[] key 
         * @param int start ��¼�Ŀ�ʼλ��(0��ʾ��һ����¼) 
         * @param int end ��¼�Ľ���λ�ã����Ϊ-1���ʾ���һ����-2��-3�Դ����ƣ� 
         * @return ִ��״̬�� 
         * */  
        public String ltrim(byte[] key, int start, int end) {  
            Jedis jedis = getJedis();  
            String str = jedis.ltrim(key, start, end);  
            returnJedis(jedis);  
            return str;  
        }  
  
        /**  
         * ����ɾ���ɣ�ֻ����start��end֮��ļ�¼ 
         * @param String key  
         * @param int start ��¼�Ŀ�ʼλ��(0��ʾ��һ����¼) 
         * @param int end ��¼�Ľ���λ�ã����Ϊ-1���ʾ���һ����-2��-3�Դ����ƣ� 
         * @return ִ��״̬�� 
         * */  
        public String ltrim(String key, int start, int end) {  
            return ltrim(SafeEncoder.encode(key), start, end);  
        }  
    }   
      
    public static void main(String[] args) {  
        JedisUtil jedisUtil= JedisUtil.getInstance();    
        JedisUtil.Strings strings=jedisUtil.new Strings();  
        strings.set("nnn", "nnnn");   
        System.out.println("-----"+strings.get("nnn"));     
          
        Jedis jedis=JedisUtil.getInstance().getJedis();   
        for (int i = 0; i < 10; i++) {   
            jedis.set("test", "test");   
            System.out.println(i+"=="+jedis.get("test"));    
          
        }  
        
        JedisUtil.Hash hash=jedisUtil.new Hash();
        hash.hsetnx("lixinmiao", "age", "29");
        hash.hsetnx("lixinmiao", "sex", "female");
        
        
        System.out.println(hash.hget("lixinmiao", "age"));    
        
        
        JedisUtil.getInstance().returnJedis(jedis);     
        
        
        
    }  
          
}  

