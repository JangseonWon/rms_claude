'use client';

import style from "@/app/(afterLogin)/request/services/[service]/multi/page.module.css";
import Order from "@/app/(afterLogin)/request/services/[service]/multi/_component/Order";
import ServiceTitle from "@/app/(afterLogin)/request/services/_component/ServiceTitle";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";
import {usePathname} from "next/navigation";
import {useEffect, useState} from "react";
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/_api/getService";

export default function Page() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [service, setService] = useState<Service>();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getService(serviceId);
            const data = await response.json();
            setService(data);
        };
        fetchData();
    }, []);

    return(
        <div className={style.container}>
            <OrderSteps/>
            <ServiceTitle serviceData={service}/>
            <section className={style.orderContainerWrapper}>
                <div className={style.orderContainer}>
                    <Order/>
                </div>
            </section>
        </div>
    )
}