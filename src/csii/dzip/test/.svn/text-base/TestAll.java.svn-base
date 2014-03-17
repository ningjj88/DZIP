package csii.dzip.test;

import csii.dzip.test.test4ATM.Deposit_Test;
import csii.dzip.test.testJincheng.DepositATCDM_test;


public class TestAll {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    String strPan="6212339010100084280,6212339010100080114,6212339010100019104,6212339010100065529," +
		"6212339010100018544,6212339010100080080,6212339010100084108,6212339010100018577,6212339010100028055," +
		"6212339010100018585,6212339010100064118,6212339010100000195,6212339010100084348,6212339010100084389," +
		"6212339010100005582,6212339010100004122,6212339010100064530,6212339010100001193,6212339010100014659";
		// TODO Auto-generated method stub
		for(int i= 0 ;i<2; i++)
		{
//			QueryForOther_Test queryForOther_Test = new QueryForOther_Test();
//			Thread queryForOther= new Thread (queryForOther_Test) ;
//			queryForOther.start();
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			QueryTran_Test queryTran_Test = new QueryTran_Test();
//			Thread queryTran= new Thread (queryTran_Test) ;
//			queryTran.start();
			
//			Deposit_Test deposit_Test = new Deposit_Test();
//			Thread deposit= new Thread (deposit_Test) ;
//			deposit.start();
			
//			Withdrawal_Test withdrawal_Test = new Withdrawal_Test();
//			Thread withdrawal= new Thread (withdrawal_Test) ;
//			withdrawal.start();
			
//			
//			Transfer_Test transfer_Test = new Transfer_Test();
//			Thread transfer= new Thread (transfer_Test) ;
//			transfer.start();
			DepositATCDM_test depositATCDM_Test = new DepositATCDM_test(strPan.substring(0,19));
			strPan=strPan.substring(20, strPan.length());
			Thread depositATCDM= new Thread (depositATCDM_Test) ;
			depositATCDM.start();
			try
			{
				Thread.sleep(1180);
			}catch(Exception e)
			{
				e.printStackTrace();
			}

		}

	}

}
