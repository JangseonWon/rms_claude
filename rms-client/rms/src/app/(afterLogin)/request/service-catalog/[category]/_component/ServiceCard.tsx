'use client';

import style from './serviceCard.module.css';
import React, {useEffect, useState} from "react";
import {useSelectCategory} from "@/store/useCategoryStore";
import {
    getServicesByCategoryId
} from "@/app/(afterLogin)/request/services/[service]/single/_api/getServicesByCategoryId";
import {Service} from "@/model/Service";
import {useRouter} from "next/navigation";

export default function ServiceCard() {
    const category = useSelectCategory();
    const [serviceData, setServiceData] = useState<Service[]>([]);
    const router = useRouter();

    const fetchData = async () => {
        const response = await getServicesByCategoryId(category?.id!);
        const data = await response.json();
        setServiceData(data as Service[]);
    }

    useEffect(() => {
        fetchData();
    }, []);

    const description = (service: string) => {
        switch(service) {
            case 'Precision Oncology':
                return `A healthy life is within reach with GC Genome.<br>
                    Analyze your health and design a roadmap for a better future.`;
            case 'Pre & Neonatal':
                return 'Safe and Accurate Solution by GC Genome.<br>' +
                    'Provide a reliable and precise approach<br>' +
                    'to every pregnant woman and baby.';
            case 'Rare Disease':
                return 'Shedding Light on the Undiagnosed by GC Genome.<br>' +
                    'Enable a more complete story for rare disease patients<br>' +
                    'with cutting edge genomic technologies.';
            case 'Health Checkup':
                return 'Making the Impossible Possible at GC Genome.<br>' +
                    'Empower patients by providing accurate and timely<br>' +
                    'information for the diagnosis and prognosis of specific cancers.';
            default:
                return '';
        }
    }

    const OrderButtonClick = (service: string) => {
        router.push(`/request/services/${service}/single`);
    }

    return (
        <div className={style.container}>
            <section className={style.bodySection}>
                {serviceData && serviceData.length > 0 ? (
                    serviceData.map((service) => (
                        <div key={service.id} className={style.categoryItem}>
                            <span className={style.categoryTitle}>{service.name}</span>
                            <div className={style.cardDescription}>
                                <span
                                    className={style.categoryDescription}
                                    dangerouslySetInnerHTML={{__html: description(service.id!)}}
                                />
                                <button className={style.orderButton} onClick={() => OrderButtonClick(service.id!)}>
                                    Order now
                                </button>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className={style.noServiceMessage}>
                        <h2>Service Not Available!</h2>
                        <p>
                            It looks like these services are not included in your current contract.<br/>
                            If you have contracted this service, please contact us via the email below<br/>
                            or through our Q&A section.<br/><br/>
                            info@gcgenome.com
                        </p>
                    </div>
                )}
            </section>
        </div>
    );
}