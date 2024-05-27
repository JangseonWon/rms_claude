"use client"

import style from "@/app/_component/blueButton.module.css"

type Props = {
    name: string
    onClick?: () => void
}
export default function BlueButton({name, onClick}: Props) {
    return (
        <button className={style.blueButton} onClick={onClick}>{name}</button>
    )
}