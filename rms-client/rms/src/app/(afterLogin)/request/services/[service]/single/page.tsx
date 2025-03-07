'use client';

import Order from "@/app/(afterLogin)/request/services/[service]/single/_component/Order";
import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css"
import ServiceTitle from "@/app/(afterLogin)/request/services/_component/ServiceTitle";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";
import {usePathname} from "next/navigation";
import GroupOrder from "@/app/(afterLogin)/request/services/[service]/single/_component/set/GroupOrder";
import {useEffect, useState} from "react";
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/request/services/[service]/single/_api/getService";

export default function Page() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [service, setService] = useState<Service>();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getService(serviceId);
            if (response.ok) {
                const data = await response.json();
                setService(data);
            }
        };
        fetchData();
    }, []);

    return(
        <div className={style.container}>
            <OrderSteps/>
            <ServiceTitle serviceData={service}/>
            <section className={style.orderContainer}>
                {service?.type != "GENERAL" ? (
                    <GroupOrder/>
                ) : (
                    <Order />
                )}
            </section>
        </div>
    )
}