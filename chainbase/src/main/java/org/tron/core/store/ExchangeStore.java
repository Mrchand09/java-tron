package org.tron.core.store;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.core.capsule.ExchangeCapsule;
import org.tron.core.db.TronStoreWithRevoking;
import org.tron.core.db.accountstate.StateType;
import org.tron.core.db.accountstate.WorldStateCallBackUtils;
import org.tron.core.exception.ItemNotFoundException;

@Component
public class ExchangeStore extends TronStoreWithRevoking<ExchangeCapsule> {

  @Autowired
  private WorldStateCallBackUtils worldStateCallBackUtils;

  @Autowired
  protected ExchangeStore(@Value("exchange") String dbName) {
    super(dbName);
  }

  @Override
  public ExchangeCapsule get(byte[] key) throws ItemNotFoundException {
    byte[] value = revokingDB.get(key);
    return new ExchangeCapsule(value);
  }

  /**
   * get all exchanges.
   */
  public List<ExchangeCapsule> getAllExchanges() {
    return Streams.stream(iterator())
        .map(Map.Entry::getValue)
        .sorted(
            (ExchangeCapsule a, ExchangeCapsule b) -> a.getCreateTime() <= b.getCreateTime() ? 1
                : -1)
        .collect(Collectors.toList());
  }

  @Override
  public void put(byte[] key, ExchangeCapsule item) {
    super.put(key, item);
    worldStateCallBackUtils.callBack(StateType.Exchange, key, item);
  }
}