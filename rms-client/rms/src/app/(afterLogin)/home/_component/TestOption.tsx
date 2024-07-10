"use client"

import style from "@/app/(afterLogin)/home/_component/testOption.module.css"
import Link from "next/link";
import Image from "next/image";
import homeMainImg from "@/../public/home_main.jpg"
import React, {useEffect, useState} from "react";
import {getCategories} from "@/app/(afterLogin)/_api/getCategories";
import {Categories} from "@/model/Categories";

export default function TestOption() {
    const [categoryData, setCategoryData] = useState<Categories[]>([]);

    const fetchData = async () => {
        const response = await getCategories()
        const data = await response.json();
        setCategoryData(data as Categories[]);
    }

    useEffect(() => {
        fetchData()
    }, []);

    return (
        <div className={style.container}>
            <div className={style.line}></div>
            <h1>Your Testing Options</h1>
            <p>Choose one of available segments to start an order using G-Portal</p>
            <div className={style.cardContainer}>
                {categoryData.map(category => (
                    <Link
                        key={category.id}
                        href={`/request/services/${category.name}/${category.order_type === 'SINGLE' ? 'single' : 'multi'}`}>
                        <div className={style.card}>
                            <Image src={homeMainImg} alt={`${category.name}`}/>
                            <div className={style.cardLabel}>{category.name}</div>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    )
}