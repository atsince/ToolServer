package com.example.huobimock;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author liyong
 * @brief description
 * @date 2021/04/25 00:12
 */
public class IncomeProcessHelper {

    public volatile BigDecimal FIRST_BUY_MONEY = new BigDecimal("20.00");
    //默认买入点位下跌率
    private  BigDecimal downBite = new BigDecimal("0.015");
    //默认卖出点位上涨率
//    private  BigDecimal upBite = new BigDecimal("0.02");
    private  BigDecimal upBite = new BigDecimal("0.02");

    int scale = 4;
    int ammount_scale = 4;
    int buyNum = 4;
    BigDecimal originPrice = new BigDecimal("14.1639");

    public IncomeProcessHelper(){

    }

    public void multiCompute(){
        originPrice = new BigDecimal("2.7116");
        buyNum = 1;
        computeIncome();
        System.out.println("====================================================");
        buyNum = 2;
        computeIncome();
        System.out.println("====================================================");

        buyNum = 3;
        computeIncome();
        System.out.println("====================================================");

        buyNum = 4;
        computeIncome();
        System.out.println("====================================================");

        buyNum = 5;
        computeIncome();
        System.out.println("====================================================");

//        originPrice = new BigDecimal("1");
//        computeIncome();
//
//        originPrice = new BigDecimal("10000");
//        computeIncome();


    }

    public void computeIncome() {


        BigDecimal totalBuyCoinCount = computeBuyCoinCount(buyNum, originPrice);
        System.out.println("===总的买入币的数量：" + totalBuyCoinCount);
        //卖出币的金额
        BigDecimal sellPrice = computSellPrice(buyNum, originPrice).setScale(scale, RoundingMode.HALF_UP);
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
        BigDecimal totalBuy = computeBuyCount(buyNum, originPrice);
        System.out.println("===实际买入花费：" + totalBuy.toPlainString());

        BigDecimal protectedPrice = totalBuy.divide(sellCount,scale, RoundingMode.HALF_UP);
        System.out.println("====保本价格："+protectedPrice.toPlainString());

        BigDecimal income = totalSell.subtract(totalBuy);
        System.out.println("收益："+income.toPlainString() + "  =========================");
    }

    //通
    //实际投入花费usdt
    private BigDecimal computeBuyCount(int buyNum, BigDecimal originPrice) {
        BigDecimal firstBuy = FIRST_BUY_MONEY;
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < buyNum; i++) {
            //约定投入
            BigDecimal co = firstBuy.multiply(BigDecimal.valueOf(Math.pow(2, i)));
            System.out.println("=======第" + i + "次买入约定花费：" + co.toPlainString());
            BigDecimal price;
//            if(i==0) {
//                price= originPrice.setScale(scale,RoundingMode.DOWN);
//            } else{
            //目标币的价格精度
            price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(downBite).doubleValue(), i )))).setScale(scale,RoundingMode.DOWN);

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

    private BigDecimal computSellPrice(int buyNum, BigDecimal originPrice) {

        if (buyNum == 1) {

            return originPrice.multiply(BigDecimal.ONE.add(upBite)).setScale(scale,RoundingMode.DOWN);
        } else if (buyNum > 1) {

            return originPrice
                    .multiply( BigDecimal.valueOf(Math.pow((BigDecimal.ONE.subtract(downBite).doubleValue()), (buyNum - 1))))
                    .multiply(BigDecimal.ONE.add(upBite))
                    .setScale(scale,RoundingMode.DOWN);
        } else {
            return BigDecimal.ZERO;
        }
    }

    //通 计算一共买入币的数量
    private BigDecimal computeBuyCoinCount(int buyNum, BigDecimal originPrice) {
        BigDecimal firstBuy = FIRST_BUY_MONEY;

        BigDecimal sum = BigDecimal.ZERO;
        if (buyNum > 0) {
            BigDecimal co = firstBuy.divide(originPrice, scale, RoundingMode.DOWN);
            sum = sum.add(co);
            System.out.println("=======第0次买入币的数量：" + co.toPlainString());

            if (buyNum > 1) {
                for (int i = 1; i < buyNum; i++) {
//                    BigDecimal price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(downBite).doubleValue(), i - 1))));
                    BigDecimal price = originPrice.multiply((BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(downBite).doubleValue(), i )))).setScale(scale,RoundingMode.DOWN);

                    BigDecimal coinCount = firstBuy.multiply(BigDecimal.valueOf(Math.pow(2, i))).divide(price, ammount_scale,RoundingMode.DOWN);
                    System.out.println("=======第" + i + "次买入币的数量：" + coinCount.toPlainString());
                    sum = sum.add(coinCount);
                }

            }
            return sum;


        }


        return BigDecimal.ZERO;

    }

}
