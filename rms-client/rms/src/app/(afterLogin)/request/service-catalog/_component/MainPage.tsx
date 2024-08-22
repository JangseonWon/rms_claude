'use client';

import style from './mainPage.module.css';
import {useCategory, useSetSelectCategory} from "@/store/useCategoryStore";
import ServiceSearchBox from "@/app/(afterLogin)/request/service-catalog/_component/ServiceSearchBox";
import React from "react";
import Link from "next/link";
import {Categories} from "@/model/Categories";
import Image from "next/image";

export default function MainPage() {
    const categoryData = useCategory();
    const setSelectCategory = useSetSelectCategory();

    const description = (name: string) => {
        switch(name) {
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

    const linkClick = (category: Categories) => {
        setSelectCategory(category);
    }

    return (
        <div className={style.container}>
            <section className={style.searchSection}>
                <div className={style.search}>
                    <ServiceSearchBox/>
                </div>
            </section>
            <section className={style.bodySection}>
                {categoryData.map((category) => (
                    <div key={category.id} className={style.categoryItem}>
                        <div className={style.cardDescription}>
                            <span className={style.categoryTitle}>{category.name}</span>
                            <span
                                className={style.categoryDescription}
                                dangerouslySetInnerHTML={{__html: description(category.name)}}
                            />
                            <Link
                                href={`/request/service-catalog/${category.id}`}
                                className={style.serviceLink}
                                onClick={() => linkClick(category)}
                            >
                                Learn more
                            </Link>
                        </div>
                        <div className={style.cardPicture}>
                            <Image src={'/category/' + category.name+ '.jpg'}
                                   alt={category.name}
                                   fill
                                   style={{ objectFit: 'cover'}}
                            />
                        </div>
                    </div>
                ))}
            </section>
        </div>
    );
}