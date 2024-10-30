'use client';

import style from './listServicePage.module.css';
import {useSelectCategory, useSetSelectCategory} from "@/store/useCategoryStore";
import ServiceSearchBox from "@/app/(afterLogin)/request/service-catalog/_component/ServiceSearchBox";
import React, {useEffect, useState} from "react";
import Image from "next/image";
import {
    getServicesByCategoryId
} from "@/app/(afterLogin)/request/services/[service]/single/_api/getServicesByCategoryId";
import {Service} from "@/model/Service";
import {Categories} from "@/model/Categories";
import {useRouter} from "next/navigation";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";
import {getCategories} from "@/app/(afterLogin)/_api/getCategories";

export default function ListServicePage() {
    const router = useRouter();
    const [categoryArray, setCategoryArray] = useState<Categories[]>();
    const [isLoading, setIsLoading] = useState(false);
    const [serviceData, setServiceData] = useState<Service[]>();
    const [selectedCard, setSelectedCard] = useState<string | null>(null);
    const selectCategory = useSelectCategory();
    const setSelectCategory = useSetSelectCategory();

    const description = (name: string) => {
        switch(name) {
            case 'Precision Oncology':
                return 'Making the Impossible Possible at GC Genome.<br>' +
                    'Empower patients by providing accurate and timely<br>' +
                    'information for the diagnosis and prognosis of specific cancers.';
            case 'Pre & Neonatal':
                return 'Safe and Accurate Solution by GC Genome.<br>' +
                    'Provide a reliable and precise approach<br>' +
                    'to every pregnant woman and baby.';
            case 'Rare Disease':
                return 'Shedding Light on the Undiagnosed by GC Genome.<br>' +
                    'Enable a more complete story for rare disease patients<br>' +
                    'with cutting edge genomic technologies.';
            case 'Health Checkup':
                return `A healthy life is within reach with GC Genome.<br>
                    Analyze your health and design a roadmap for a better future.`;
            default:
                return '';
        }
    }

    const cardOnClick = (category: Categories) => {
        setSelectedCard(category.id!);
        setSelectCategory(category);
    }

    const serviceOnClick = async (service: Service) => {
        setIsLoading(true);
        try {
            await new Promise(resolve => setTimeout(resolve, 500));
            router.push(`/request/services/${service.id}/single`);
        } finally {
            setIsLoading(false);
        }
    }

    const fetchServiceData = async (categoryId: string) => {
        const response = await getServicesByCategoryId(categoryId);
        const data = await response.json();
        setServiceData(data as Service[]);
    }

    const fetchCategoryData = async () => {
        const response = await getCategories()
        const data = await response.json();
        setCategoryArray(data as Categories[]);
    }

    useEffect(() => {
        const fetchData = async () => {
            await fetchCategoryData();
        };

        fetchData();
    }, []);

    useEffect(() => {
        if (categoryArray && categoryArray.length > 0) {
            const firstCategory = categoryArray[0];
            setSelectCategory(firstCategory);
            setSelectedCard(firstCategory.id!);
            fetchServiceData(firstCategory.id!);
        }
    }, [categoryArray]);

    useEffect(() => {
        if (selectCategory) {
            setServiceData([]);
            fetchServiceData(selectCategory.id!);
        }
    }, [selectCategory]);

    return (
        <div className={style.container}>
            {isLoading && <QnaLoading/>}
            <section className={style.bodySection}>
                <div className={style.search}>
                    <ServiceSearchBox/>
                </div>
                <section className={style.categorySection}>
                    {categoryArray && categoryArray.length > 0 && categoryArray.map((category) => (
                        <div key={category.id}
                             className={`${style.categoryItem} ${selectedCard === category.id ? style.selectedCard : ''}`}
                             onClick={() => cardOnClick(category)}
                        >
                            <div className={style.cardContainer}>
                                <div className={style.cartTopSection}>
                                    <div className={style.cardDescription}>
                                        <span
                                            className={`${style.categoryTitle} ${selectedCard === category.id ? style.selectedTitle : ''}`}>
                                            {category.name}
                                        </span>
                                        <span
                                            className={`${style.categoryDescription} ${selectedCard === category.id ? style.selectedDescriptionText : ''}`}
                                            dangerouslySetInnerHTML={{__html: description(category.name)}}
                                        />
                                    </div>
                                    <div
                                        className={`${style.cardPicture} ${selectedCard === category.id ? style.selectedCardPicture : ''}`}>
                                        <Image src={'/category/' + category.name + '.jpg'}
                                               alt={category.name}
                                               fill
                                        />
                                    </div>
                                </div>
                                <div className={`${style.cartBottomSection} ${selectedCard === category.id ? style.expanded : ''}`}>
                                    <div className={style.cartBottomSectionScroll}>
                                        {serviceData && serviceData.length > 0 ? (serviceData?.map((service) => (
                                                <div key={service.id} className={style.serviceLink} onClick={()=> serviceOnClick(service)}>
                                                    {service.name}
                                                </div>
                                            ))
                                        ) : (
                                            <div className={style.noServicesMessage}>
                                                The service does not exist.
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </section>
            </section>
        </div>
    );
}