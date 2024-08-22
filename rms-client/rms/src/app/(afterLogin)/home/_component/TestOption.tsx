"use client"

import style from "@/app/(afterLogin)/home/_component/testOption.module.css"
import Link from "next/link";
import Image from "next/image";
import homeMainImg from "@/../public/home_main.jpg"
import React, {useEffect} from "react";
import {getCategories} from "@/app/(afterLogin)/_api/getCategories";
import {Categories} from "@/model/Categories";
import {useCategory, useSetCategory} from "@/store/useCategoryStore";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

export default function TestOption() {
    const categoryData = useCategory();
    const setCategoryData = useSetCategory();

    const fetchData = async () => {
        const response = await getCategories()
        const data = await response.json();
        setCategoryData(data as Categories[]);
    }

    useEffect(() => {
        fetchData()
    }, []);

    const settings = {
        infinite: true,
        speed: 500,
        slidesToShow: 4,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 5000,
        arrows: true,
        nextArrow: <div className={style.nextArrow}>&gt;</div>,
        prevArrow: <div className={style.prevArrow}>&lt;</div>,
    };

    return (
        <div className={style.container}>
            <div className={style.line}></div>
            <h1>Your Testing Options</h1>
            <p>Choose one of available segments to start an order using G-Portal</p>
            <Slider className={style.slider} {...settings}>
                {categoryData && categoryData.length > 0 && categoryData.map(category => (
                    <Link
                        key={category.id}
                        href={`/request/services/${category.id}/single`}>
                        <div className={style.card}>
                            <Image src={homeMainImg} alt={`${category.name}`}/>
                            <div className={style.cardLabel}>{category.name}</div>
                        </div>
                    </Link>
                ))}
            </Slider>
        </div>
    )
}