package chapter8;

public interface CoordinatedAccountService {
    void transfer(final CoordinatedAccount from, final CoordinatedAccount to, final int amount);
}
