'use client';

import style from "./catalogTitle.module.css";
import {usePathname} from "next/navigation";
import React, {useEffect, useState} from "react";
import {getCategoryById} from "@/app/(afterLogin)/request/service-catalog/[category]/_api/getCategoryById";
import {Categories} from "@/model/Categories";
import Image from "next/image";

export default function CatalogTitle() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const lastValue = decodeURIComponent(pathSegments.pop() || '');
    const [categoryData, setCategoryData] = useState<Categories>();

    const description = (name: string) => {
        switch(name) {
            case 'Precision Oncology':
                return {
                    first: 'A targeted approach for personalized treatment.',
                    second: 'Empower patients with the most precise genomic data.',
                };
            case 'Pre & Neonatal':
                return {
                    first: 'Safe and accurate solutions for mother and baby.',
                    second: 'Ensuring the best start for the newest life.',
                };
            case 'Rare Disease':
                return {
                    first: 'Shedding light on the undiagnosed.',
                    second: 'Providing clarity through advanced genomic analysis.',
                };
            case 'Health Checkup':
                return {
                    first: 'A healthy life is within reach with GC Genome.',
                    second: 'Analyze your health and design a roadmap for a better future.',
                };
            default:
                return {
                    first: '',
                    second: '',
                };
        }
    }


    useEffect(() => {
        const fetchData = async () => {
            const response = await getCategoryById(lastValue);
            const data = await response.json();
            setCategoryData(data);
        };
        fetchData()
    }, []);

    if (!categoryData) {
        return <div>Loading...</div>;
    }

    const { first, second } = description(categoryData?.name || '');
    const title = categoryData?.name || 'not found';

    return (
        <div className={style.title}>
            <div className={style.first}>
                <div className={style.subTitle}>
                    Service Catalog &gt; <span>{title}</span>
                </div>
                <div className={style.mainTitle}>
                    {title}
                </div>
            </div>
            <div className={style.second}>
                <div className={style.sentence}>
                    <span className={style.firstSentence}>{first}</span>
                    <span className={style.secondSentence}>{second}</span>
                </div>
                <div className={style.picture}>
                    <Image src={'/category/' + categoryData.name + '.jpg'}
                           alt={categoryData.name}
                           fill
                    />
                    <div className={style.gradientOverlay}></div>
                </div>
            </div>
        </div>
    )
}