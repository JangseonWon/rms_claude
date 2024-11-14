'use client';

import style from './orderSteps.module.css';
import React from "react";
import {usePathname} from "next/navigation";

export default function OrderSteps() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const service = decodeURIComponent(pathSegments[pathSegments.length - 1]);

    const styleByStep = (...steps: string[]) => {
        return steps.includes(service) ? style.activeStep : '';
    };

    return (
        <div className={style.container}>
            <div className={style.innerContainer}>
                <div className={style.orderSteps}>
                    Order steps
                </div>
                <div className={`${style.stepContainer} ${styleByStep('service-catalog')}`}>
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
                <div className={`${style.stepContainer} ${styleByStep('single', 'multi')}`}>
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
                <div className={`${style.stepContainer} ${styleByStep('cart')}`}>
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
                <div className={`${style.stepContainer} ${styleByStep('order')}`}>
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