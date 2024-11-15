'use client';

import style from './orderSteps.module.css';
import React from "react";
import {usePathname, useRouter} from "next/navigation";

export default function OrderSteps() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const service = decodeURIComponent(pathSegments[pathSegments.length - 1]);
    const router = useRouter();

    const styleByStep = (...steps: string[]) => {
        return steps.includes(service) ? style.activeStep : '';
    };

    const stepClick = (step: string) => {
        switch (step) {
            case 'service-catalog' :
                return router.push('/request/service-catalog');
            case 'single' :
                return router.push('/request/service-catalog');
            case 'cart' :
                return router.push('/request/cart');
            case 'order' :
                return router.push('/request/order');
        }
    }

    return (
        <div className={style.container}>
            <div className={style.innerContainer}>
                <div className={style.orderSteps}>
                    Order steps
                </div>
                <div className={`${style.stepContainer} ${styleByStep('service-catalog')}`}
                    onClick={() => stepClick('service-catalog')}>
                    <div className={style.circleNumber}>
                        1
                    </div>
                    <div className={style.textContainer}>
                        <span>
                            Service
                        </span>
                        <span>
                            Selection
                        </span>
                    </div>
                    <div className={style.line}/>
                </div>
                <div className={`${style.stepContainer} ${styleByStep('single', 'multi')}`}
                     onClick={() => stepClick('single')}>
                    <div className={style.circleNumber}>
                        2
                    </div>
                    <div className={style.textContainer}>
                        <span>
                            Patient Info
                        </span>
                    </div>
                    <div className={style.line}/>
                </div>
                <div className={`${style.stepContainer} ${styleByStep('cart')}`}
                     onClick={() => stepClick('cart')}>
                    <div className={style.circleNumber}>
                        3
                    </div>
                    <div className={style.textContainer}>
                        <span>
                            Cart
                        </span>
                    </div>
                    <div className={style.line}/>
                </div>
                <div className={`${style.stepContainer} ${styleByStep('barcode')}`}>
                    <div className={style.circleNumber}>
                        4
                    </div>
                    <div className={style.textContainer}>
                        <span>
                            Barcode
                        </span>
                        <span>
                            Generation
                        </span>
                    </div>
                    <div className={style.line}/>
                </div>
                <div className={`${style.stepContainer} ${styleByStep('order')}`}
                     onClick={() => stepClick('order')}>
                    <div className={style.circleNumber}>
                        5
                    </div>
                    <div className={style.textContainer}>
                        <span>
                            Order
                        </span>
                        <span>
                            Confirmation
                        </span>
                    </div>
                </div>
            </div>
        </div>
    )
}