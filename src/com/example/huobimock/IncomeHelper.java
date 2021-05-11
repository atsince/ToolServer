package com.example.huobimock;


import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 预算收益 计算器
 */
class IncomeHelper {

    public volatile BigDecimal FIRST_BUY_MONEY = new BigDecimal("20.00");
    //默认买入点位下跌率
    public final BigDecimal DEFAULT_DOWN_BITE = new BigDecimal("0.025");
    //默认卖出点位上涨率
    public final BigDecimal DEFAULT_UP_BITE = new BigDecimal("0.03");

    int scale = 4;
    int ammount_scale = 4;

    int buyCount = 2;
    BigDecimal originPrice = new BigDecimal("14.1639");

    public void multiCompute(){
        originPrice = new BigDecimal("2.7116");
        buyCount = 1;
        computeIncome();
        System.out.println("====================================================");
        buyCount = 2;
        computeIncome();
        System.out.println("====================================================");

        buyCount = 3;
        computeIncome();
        System.out.println("====================================================");

        buyCount = 4;
        computeIncome();
        System.out.println("====================================================");

        buyCount = 5;
        computeIncome();
        System.out.println("====================================================");

//        originPrice = new BigDecimal("1");
//        computeIncome();
//
//        originPrice = new BigDecimal("10000");
//        computeIncome();


    }


    void computeIncome() {


        BigDecimal totalBuyCoinCount = computeBuyCoinCount(buyCount, originPrice);
        System.out.println("===总的买入币的数量：" + totalBuyCoinCount);

        //卖出币的金额
        BigDecimal sellPrice = computSellPrice(buyCount, originPrice).setScale(scale,RoundingMode.HALF_UP);
        System.out.println("===卖出价格：" + sellPrice.toPlainString());
        BigDecimal sellCount = totalBuyCoinCount.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.002"))).setScale(ammount_scale,RoundingMode.HALF_UP);//改一次
        System.out.println("===卖出的数量：" + sellCount.toPlainString());

        BigDecimal totalSell = sellCount.multiply(sellPrice);
        System.out.println("===成交额：" + totalSell.toPlainString());


        //服务费
        BigDecimal serviceFee = totalSell.multiply(new BigDecimal("0.002")).setScale(8,RoundingMode.DOWN);//usdt的个数精度
//        BigDecimal serviceFee = totalSell.multiply(new BigDecimal("0.002"));//usdt的个数精度
        System.out.println("===卖出手续费：" + serviceFee.toPlainString());

        //买入金额
        BigDecimal totalBuy = computeBuyCount(buyCount, originPrice);
        System.out.println("===实际买入花费：" + totalBuy.toPlainString());

        BigDecimal income = totalSell.subtract(totalBuy);
        System.out.println("收益："+income.toPlainString() + "  =========================");
    }

    //通
    //实际投入花费usdt
    private BigDecimal computeBuyCount(int buyCount, BigDecimal originPrice) {
        BigDecimal firstBuy = FIRST_BUY_MONEY;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < buyCount; i++) {
            //约定投入
            BigDecimal co = firstBuy.multiply(BigDecimal.valueOf(Math.pow(2, i)));
            System.out.println("=======第" + i + "次买入约定花费：" + co.toPlainString());
            BigDecimal price;
//            if(i==0) {
//                price= originPrice.setScale(scale,RoundingMode.DOWN);
//            } else{
            //目标币的价格精度
            price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(DEFAULT_DOWN_BITE).doubleValue(), i )))).setScale(scale,RoundingMode.DOWN);

//            }
            System.out.println("=======第" + i + "次买入价格：" + price.toPlainString());


            BigDecimal count = co.divide(price, ammount_scale, RoundingMode.DOWN);
            System.out.println("=======第" + i + "次买入币的数量：" + count.toPlainString());

            BigDecimal se = count.multiply(new BigDecimal("0.002")).setScale(8,RoundingMode.HALF_UP);
            System.out.println("=======第" + i + "次买入币的手续费数量：" + se.toPlainString());

            BigDecimal succ = count.multiply(price);
            System.out.println("=======第" + i + "次买入实际成交额：" + succ.toPlainString());

            BigDecimal real = (count.add(se)).multiply(price);
            System.out.println("=======第" + i + "次买入实际花费：" + real.toPlainString());


            //实际投入
            sum = sum.add(real);
        }
        return sum;
    }

    private BigDecimal computSellPrice(int buyCount, BigDecimal originPrice) {

        if (buyCount == 1) {

            return originPrice.multiply(BigDecimal.ONE.add(DEFAULT_UP_BITE)).setScale(scale,RoundingMode.DOWN);
        } else if (buyCount > 1) {

            return originPrice
                    .multiply( BigDecimal.valueOf(Math.pow((BigDecimal.ONE.subtract(DEFAULT_DOWN_BITE).doubleValue()), (buyCount - 1))))
                    .multiply(BigDecimal.ONE.add(DEFAULT_UP_BITE))
                    .setScale(scale,RoundingMode.DOWN);
        } else {
            return BigDecimal.ZERO;
        }
    }

    //通 计算一共买入币的数量
    private BigDecimal computeBuyCoinCount(int buyCount, BigDecimal originPrice) {
        BigDecimal firstBuy = FIRST_BUY_MONEY;

        BigDecimal sum = BigDecimal.ZERO;
        if (buyCount > 0) {
            BigDecimal co = firstBuy.divide(originPrice, scale, RoundingMode.DOWN);
            sum = sum.add(co);
            System.out.println("=======第0次买入币的数量：" + co.toPlainString());

            if (buyCount > 1) {
                for (int i = 1; i < buyCount; i++) {
//                    BigDecimal price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(DEFAULT_DOWN_BITE).doubleValue(), i - 1))));
                    BigDecimal price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(DEFAULT_DOWN_BITE).doubleValue(), i )))).setScale(scale,RoundingMode.DOWN);

                    BigDecimal coinCount = firstBuy.multiply(BigDecimal.valueOf(Math.pow(2, i))).divide(price, ammount_scale,RoundingMode.DOWN);
                    System.out.println("=======第" + i + "次买入币的数量：" + coinCount.toPlainString());
                    sum = sum.add(coinCount);
                }

            }
            return sum;


        }


        return BigDecimal.ZERO;

    }


    void contextLoads() {
        System.out.println("  ");
        System.out.println("  ");
        System.out.println("  ");

        float x = 0.0045f;

        int a = 100;//给定值
        int unit = 1;//买入起始值


//        int n = 5;

        //先求出最多买几次
        int n = 0;
        int sum = 0;
        while (sum < a) {
            sum += unit * Math.pow(2, n);
            n++;

        }

        System.out.println("n = " + n);
        System.out.println("可买次数 " + (n - 1));


        //再求不可买入时，持仓+可用 还值多少钱
        int count = n - 1;
        double last = 0;
        double sum2 = 0;
        for (int i = 0; i < count; i++) {
            sum2 = last * (1 - x) + unit * Math.pow(2, i);
            last = sum2;
        }

        double buyTotal = unit * (Math.pow(2, count) - 1);
        System.out.println("总投入： " + buyTotal + " 现价：" + sum2);
        System.out.println("亏损点数 " + (1 - sum2 / buyTotal));


        //猜测：亏损点数可提前预知；   与x，交易次数有关,与初始投入无关。

        //证明过程
        unit = 165;
        int c = 6;
        double l = 0;
        double s = 0;
        for (int i = 0; i < count; i++) {
            s = l * (1 - x) + unit * Math.pow(2, i);
            l = s;
        }

        double t = unit * (Math.pow(2, c) - 1);
        System.out.println("2222总投入： " + t + " 现价：" + s);
        System.out.println("2222亏损点数 " + (1 - s / t));


        //价值分析  有啥价值
        //可设定平仓策略， 设定阈值，查出指定阈值，强行平仓
        //具体：不可买入后，如果继续下跌。下跌到最后一次购买价格的10x，截断平仓


        //
        BigDecimal value = new BigDecimal("46.258").setScale(2, RoundingMode.DOWN);

        System.out.println("value = " + value);

        System.out.println("  ");
        System.out.println("  ");
        System.out.println("  ");


    }

}
