'use client';

import style from './listServicePage.module.css';
import {useSelectCategory, useSetSelectCategory} from "@/store/useCategoryStore";
import React, {useEffect, useState} from "react";
import Image from "next/image";
import {getServicesByCategoryId} from "@/app/(afterLogin)/request/services/[service]/single/_api/getServicesByCategoryId";
import {Service} from "@/model/Service";
import {Categories} from "@/model/Categories";
import {useRouter} from "next/navigation";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";
import {getCategories} from "@/app/(afterLogin)/_api/getCategories";
import SearchSelectBox, {Option} from "@/app/_component/SearchSelectBox";
import {getServices} from "@/app/(afterLogin)/request/service-catalog/_api/getServices";

export default function ListServicePage() {
    const router = useRouter();
    const [categoryArray, setCategoryArray] = useState<Categories[]>();
    const [serviceOptions, setServiceOptions] = useState<Option[]>([])
    const [isLoading, setIsLoading] = useState(false);
    const [serviceData, setServiceData] = useState<Service[]>();
    const [selectedCard, setSelectedCard] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
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

    const handleSelect = async (option: any) => {
        if (option && option.id) {
            setIsLoading(true);
            try {
                await new Promise(resolve => setTimeout(resolve, 500));
                router.push(`/request/services/${option.id}/single`);
            } finally {
                setIsLoading(false);
            }
        }
    };

    const serviceOnClick = async (service: Service) => {
        setIsLoading(true);
        try {
            await new Promise(resolve => setTimeout(resolve, 500));
            router.push(`/request/services/${service.id}/single`);
        } finally {
            setIsLoading(false);
        }
    }

    const fetchServices = async () => {
        const response = await getServices();
        if (response.ok) {
            const data = await response.json();
            const services = data as Service[]
            const mappedOptions = services.map(mapServiceToOption)
            setServiceOptions(mappedOptions)
        } else {
            return [];
        }
    }
    const mapServiceToOption = (service: Service): Option => ({
        id: service.id!,
        label: service.name!,
    });

    const fetchServiceData = async (categoryId: string) => {
        const response = await getServicesByCategoryId(categoryId);
        if (response.ok) {
            const data = await response.json();
            setServiceData(data as Service[]);
        } else {
            setServiceData([]);
        }
    }

    const fetchCategoryData = async () => {
        const response = await getCategories();
        if (response.ok) {
            const data = await response.json();
            setCategoryArray(data as Categories[]);
        } else {
            setCategoryArray([]);
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            await fetchCategoryData();
        };
        fetchData();
        fetchServices();
    }, []);

    useEffect(() => {
        if (categoryArray && categoryArray.length > 0) {
            const firstCategory = categoryArray[0];
            setSelectCategory(firstCategory);
            setSelectedCard(firstCategory.id!);
            fetchServiceData(firstCategory.id!);

            const {hash} = window.location;

            const categoryOffsets: { [key: string]: number } = {
                "38fecf42-1404-490f-ab97-37ed7eeecd78": 200,
                "9b488043-ee87-447a-bd9b-000815fb0e98": 400,
                "a57e0b55-ee39-4544-a835-74b5aa4a25ef": 600,
                "e3205ea8-5f6b-4731-9871-4fcfed5382cc": 800,
                default: 100,
            };

            setTimeout(() => {
                if (hash) {
                    const hashId = hash.replace("#", "");
                    const matchedCategory = categoryArray.find(category => category.id === hashId);

                    if (matchedCategory) {
                        cardOnClick(matchedCategory);
                        const target = document.getElementById(hashId);
                        if (target) {
                            const offset = categoryOffsets[hashId] || categoryOffsets.default;
                            window.scrollTo({
                                top: offset,
                                behavior: "smooth",
                            });
                        }
                    }
                } else {
                    const firstCategory = categoryArray[0];
                    setSelectCategory(firstCategory);
                    setSelectedCard(firstCategory.id!);
                    fetchServiceData(firstCategory.id!);
                }
            }, 100);
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
                    <SearchSelectBox
                        options={serviceOptions}
                        onSelect={handleSelect}
                        placeholder={'Service name...'}
                        value={searchTerm}
                        onChange={setSearchTerm}
                    />
                </div>
                <section className={style.categorySection}>
                    {categoryArray && categoryArray.length > 0 && categoryArray.map((category) => (
                        <div key={category.id}
                             id={category.id}
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
                                                <div key={service.id} className={style.serviceLink}
                                                     onClick={() => serviceOnClick(service)}>
                                                    <span className={style.serviceCode}>{service.id}</span>
                                                    <span className={style.serviceName}>{service.name}</span>
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