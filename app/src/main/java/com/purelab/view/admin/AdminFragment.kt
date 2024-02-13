package com.purelab.view.admin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.purelab.R
import com.purelab.databinding.AdminInputSectionBinding
import com.purelab.databinding.FragmentAdminBinding
import com.purelab.utils.CustomSnackbar
import com.purelab.view.BaseDataBindingFragment

class AdminFragment : BaseDataBindingFragment<FragmentAdminBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_admin
    private val vm: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAdminBinding.inflate(inflater, container, false)

        // エラーメッセージをバインド
        vm.firestoreRepository.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                // エラーメッセージがnullでない場合にダイアログを表示
                context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(it)
                        .setPositiveButton("OK", null)
                        .show()
                }

                // エラーメッセージをnullにリセットして、同じエラーのダイアログが再表示されるのを防ぐ
                vm.firestoreRepository.errorMessage.value = null
            }
        }

        // 全データのロード
        vm.loadData()

        // キーボードの設定
        settingKeyBord(binding.adminName)

        // 各セクションの描画
        setItemSection()
        setupInputSection(binding.brandInputSection, "ブランド")
        setupInputSection(binding.categoryInputSection, "カテゴリ")
        setupInputSection(binding.ingredientInputSection, "成分")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 各種ボタンの設定
        setIngredientButton()
        setupItemSaveButton()
        setupBrandSaveButton(binding.brandInputSection)
        setupCategorySaveButton(binding.categoryInputSection)
        setupIngredientSaveButton(binding.ingredientInputSection)
    }

    /** 商品を追加セクションの描画設定 */
    private fun setItemSection() {
        // ブランドのピッカーを設定
        vm.brands.observe(viewLifecycleOwner) { brandMap ->
            val brandList = brandMap.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, brandList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminBrand.adapter = adapter
        }

        // カテゴリのピッカーを設定
        vm.categories.observe(viewLifecycleOwner) { categoryMap ->
            val categoryList = categoryMap.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, categoryList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.adminCategory.adapter = adapter
        }

        // 成分のピッカーを設定
        vm.selectedIngredients.observe(viewLifecycleOwner) { ingredients ->
            val text = if (ingredients.isEmpty()) {
                "成分を選択"
            } else {
                ingredients.joinToString(separator = "\n")
            }
            binding.adminIngredients.text = text
        }
    }

    /** 各セクションのレイアウトを描画 */
    private fun setupInputSection(
        section: AdminInputSectionBinding,
        label: String
    ) {
        section.titleText.text = label + "を追加"
        section.labelText.text = label + "名"
        section.inputField.hint = label + "名を入力"
        section.saveButton.text = "保存"
        settingKeyBord(section.inputField)
    }

    /** 商品追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupItemSaveButton() {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.adminName.text.toString()
            val brandingID = binding.adminBrand.textAlignment.toString()
            val categoryID = binding.adminCategory.textAlignment.toString()
            val ingredientIDs = listOf(vm.selectedIngredients.value)

            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "商品名を入力してください")
                return@setOnClickListener
            } else if (brandingID == "") {
                CustomSnackbar.showSnackBar(v, "ブランド名を入力してください")
                return@setOnClickListener
            } else if (categoryID == "") {
                CustomSnackbar.showSnackBar(v, "カテゴリを選択してください")
                return@setOnClickListener
            } else if (ingredientIDs.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "成分を選択してください")
                return@setOnClickListener
            }

            val product = hashMapOf(
                "name" to name,
                "brandID" to brandingID,
                "categoryID" to categoryID,
                "ingredientIDs" to ingredientIDs
            )
            vm.saveItem(createData())

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "商品を保存しました")
        }
    }

    private fun createData(): List<Map<String, Any>> {

        val list = listOf(
            mapOf<String, Any>(
                "name" to "いつかの石けん",
                "brandID" to "30022",
                "categoryID" to "20001",
                "description" to
                        "「いつかの石けん」は、使ううちに毛穴が激変する酵素洗顔石けんです★" +
                        "「汚れを洗い流すこと」を徹底的に突き詰め、合成界面活性剤を使用しない天然成分にこだわっています。\n" +
                        "「いつかの石けん」には、タンパク質分解酵素が含まれており、肌の再生を促すといわれています。\n" +
                        "洗顔石鹸本来の役割＝洗い流すことを考え、余分なものを一切加えずに安心できる素材のみで作られています♪\n" +
                        "独特の優しい肌触りです。",
                "ingredientIDs" to listOf(
                    "40458", "40135", "40187", "40321", "40064", "40436", "40385", "40086", "40444"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "カネボウ スクラビング マッド ウォッシュ",
                "brandId" to "30004",
                "categoryId" to "20001",
                "description" to
                        "研ぎ澄ましたようにクリアな素肌へ導く、吸着磨き上げ洗顔。 肌に吸い付くような生泥感触のペーストの三段階の質感変化で、解き放たれるようにすべらかで、うるおい感のある明るい肌へ。 濃密な泡での泡洗顔も叶えながら、取り去りたいという衝動を毎日迷いなく解放する、吸着磨き上げ洗顔です。 クレイ+スクラブ(洗浄成分)を配合したペーストで蓄積汚れを吸着・肌を磨き上げる モロッコ溶岩クレイ(洗浄成分)高配合*のペーストに崩壊性スクラブ(洗浄成分)を配合。 余計な皮脂を吸着し、古い角質や毛穴汚れをすっきり取り去ります。「すっきり落とし切りたい!」衝動を解放し、うるおい感のある、クリアな肌へ。",
                "ingredientIDs" to listOf(
                    "40135",
                    "40362",
                    "40444",
                    "40446",
                    "40384",
                    "40026",
                    "40028",
                    "40285",
                    "40370",
                    "40016"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ディープクリア酵素洗顔",
                "brandId" to "30067",
                "categoryId" to "20001",
                "description" to "\"毎日使えるチューブタイプの酵素洗顔。タンパク汚れをしっかり落とすタンパク分解酵素を配合。\n" +
                        "さらに、ピュアビタミンCを配合することにより、うるおいを守りながら、毛穴汚れを除去して、つるんとした透明感のある肌に導きます。保湿剤を多量配合しているので、うるおいながらも、クレイ(カオリン)配合の吸着泡が、毛穴汚れを効果的にからめとります。\n" +
                        "チューブでの酵素安定化で特許取得済み。\"",
                " ingredientIDs " to listOf(
                    "40135",
                    "40444",
                    "40285",
                    "40362",
                    "40446",
                    "40010",
                    "40170",
                    "40393",
                    "40193",
                    "40391"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "エッセンスイン　クレンジングフォーム",
                "brandId" to "30002",
                "categoryId" to "20001",
                "description" to "\"肌荒れの原因まで落とす　薬用美容洗顔料\n" +
                        "美容成分配合のクリーミーでたっぷりのクッション泡が肌をやさしく包みこみ、汚れや肌荒れの原因までしっかりオフ。\n" +
                        "花粉・ちり・ほこりなどの微粒子汚れも取り除きます。\n" +
                        "健やかな肌に大切な美肌菌を残し、うるおいを守りながら、しっとりとなめらかに洗いあげます。\n" +
                        "薬用有効成分配合で、ニキビ・肌荒れを防ぎます。\n" +
                        "\n" +
                        "●厳選成分配合、クリーン製法\n" +
                        "●パラベン（防腐剤）フリー、アルコール（エチルアルコール）フリー、鉱物油フリー\n" +
                        "●無香料、無着色\n" +
                        "●低刺激設計。敏感肌の方のご協力によるパッチテスト済み*\n" +
                        "●スティンギングテスト済み*\n" +
                        "*すべての方にアレルギーや皮ふ刺激がおきないわけではありません。\"",
                "ingredientIDs" to listOf(
                    "40138",
                    "40467",
                    "40094",
                    "40127",
                    "40457",
                    "40362",
                    "40218",
                    "40339",
                    "40448",
                    "40193"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ポア クリアリング ジェル ウォッシュ",
                "brandId" to "30012",
                "categoryId" to "20001",
                "description" to "\"なかなか落としきれない置き去り角栓※1を内側まで崩壊し洗浄する、週1・2回の集中ケア洗顔料。毛穴の黒ずみ汚れ・ざらつきを落とし、洗うたび、瞬間つるん。\n" +
                        "\n" +
                        "●肌のうるおいを守る、美容成分配合(保湿:キハダ樹皮エキス、マンニトール、トレハロース)\n" +
                        "●キレイが弾ける、クリアオーシャンエナジーの香り\n" +
                        "●鼻・頬・あごなどの毛穴汚れが気になる部分に週1・2回を目安に。\n" +
                        "\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40219",
                    "40266",
                    "40265",
                    "40359",
                    "40036",
                    "40062",
                    "40111",
                    "40459",
                    "40128"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "クラリファイイング ジェル ウォッシュ",
                "brandId" to "30029",
                "categoryId" to "20001",
                "description" to "\"くすみの原因*を洗い流し、澄んだ明るい素肌へ洗い上げるジェル洗顔料。\n" +
                        "ぷるんと厚みのあるジェルが肌になめらかに密着。\n" +
                        "毛穴に詰まった角栓を分解して落とし、酸化タンパク質を含む古い角質や皮脂までしっかり洗い流します。\n" +
                        "潤いを守って、なめらかな、透明感のある洗い上がり。\n" +
                        "【Wクラリファイイングテクノロジー採用】\n" +
                        "●CPコンプレックスα(保湿:トコフェロール、ソルビトール)配合　\n" +
                        "*古い角質や皮脂汚れ\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40219",
                    "40266",
                    "40265",
                    "40036",
                    "40359",
                    "40062",
                    "40309",
                    "40228",
                    "40244"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "泥ジェル洗顔",
                "brandId" to "30061",
                "categoryId" to "20001",
                "description" to "\"自信が持てるすっぴん肌に。\n" +
                        "黒の\"\"泥ジェル洗顔\"\"で、毛穴徹底メンテ!\n" +
                        "\n" +
                        "毛穴の汚れや小鼻のザラつきはしっかりとオフしつつ、頬はぷるるん! すっきり&うるおい感を両立した\"\"絶妙つるスベ肌\"\"に洗い上げる、泡立ていらずの洗顔料です。乾燥や肌への摩擦などの刺激を気にせず、毎日の洗顔で毛穴の目立ちにアプローチできます。\n" +
                        "しかも、たった1本で、洗顔・マッサージ・パックまでできるから、気分や肌状態に合わせてスペシャルケアまで叶う優れモノ。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40332",
                    "40010",
                    "40219",
                    "40153",
                    "40078",
                    "40331",
                    "40424",
                    "40111"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ディープクリア洗顔パウダー ",
                "brandId" to "30061",
                "categoryId" to "20001",
                "description" to "\"毛穴汚れごっそり!なのに、つっぱらない。\n" +
                        "大人気「黒の酵素洗顔」\n" +
                        "\n" +
                        "『ディープクリア 洗顔パウダー』は、2019年の発売以来、毎年@cosmeのベストコスメにランクインを続けてきた人気アイテム。酵素と炭や吸着泥のチカラで毛穴の汚れをしっかりオフする洗顔パウダーです。美肌の印象を損なう要因とされる毛穴の黒ずみや角栓をすっきり落としながら、肌のうるおいを守ります。\"",
                "ingredientIDs" to listOf(
                    "40152",
                    "40359",
                    "40005",
                    "40031",
                    "40233",
                    "40320",
                    "40397",
                    "40363",
                    "40125",
                    "40279"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ビューティクリア パウダーウォッシュN",
                "brandId" to "30044",
                "categoryId" to "20001",
                "description" to "\"2つの酵素&アミノ酸系洗浄成分配合※で毛穴の黒ずみ汚れ・角栓・ザラつき・古い角質をうるおいを守りながら取り去って、洗うたび透明感がアップ。\n" +
                        "つるつるすべすべな素肌に洗い上げる酵素洗顔パウダー。お肌を洗浄し、ニキビを防ぎます。\n" +
                        "※洗浄成分:タンパク分解酵素(プロテアーゼ)、皮脂分解酵素(リパーゼ)、アミノ酸系洗浄成分(ラウロイルグルタミン酸Na、ミリストイルグルタミン酸Na)\"",
                "ingredientIDs" to listOf(
                    "40226",
                    "40151",
                    "40108",
                    "40397",
                    "40394",
                    "40367",
                    "40120",
                    "40189",
                    "40369",
                    "40067"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "クレイ ブラン ハーバル フェイスウォッシュ ",
                "brandId" to "30041",
                "categoryId" to "20001",
                "description" to "\"泥のように濃密でクリーミーな泡が肌に密着。毛穴の奥の皮脂汚れや古い角質まで、すっきりオフする洗顔料。\n" +
                        "\n" +
                        "古来ハーブの知恵と、科学の力\n" +
                        "テカリ・毛穴目立ち・乾燥をケア\n" +
                        "\n" +
                        "泥のように濃密で、吸着力の高いクリーミーな泡立ち。\n" +
                        "毛穴詰まりやニキビのもととなる、余分な皮脂や古い角質を落とします。\n" +
                        "洗い流した後は、しっとりとした明るくすべすべの肌に。\n" +
                        "\n" +
                        "厳選したオーガニック植物エキスとシソ科ハーブエキス:\n" +
                        "オーガニックカレンデュラエキス\n" +
                        "オーガニックカミツレ花エキス\n" +
                        "オーガニックラベンダー花エキス\n" +
                        "オーガニックペパーミント葉エキス\n" +
                        "オーガニックセージ葉エキス\n" +
                        "アップルミント葉エキス\n" +
                        "\n" +
                        "フレッシュハーバルの香り:\n" +
                        "ナチュラルなハーブ感が広がる、みずみずしくフレッシュな香りです。\n" +
                        "\n" +
                        "オーガニックカレンデュラエキス(トウキンセンカ花エキス)、オーガニックカミツレ花エキス(カミツレ花エキス)、オーガニックラベンダー花エキス(ラベンダー花エキス)\n" +
                        "オーガニックペパーミント葉エキス(セイヨウハッカ葉エキス)、オーガニックセージ葉エキス(セージ葉エキス)、アップルミント葉エキス、紫茶エキス(チャ葉エキス)グリセリン(保湿)配合\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40362",
                    "40135",
                    "40193",
                    "40446",
                    "40393",
                    "40026",
                    "40028",
                    "40152",
                    "40390"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "スキンクリア クレンズ オイル アロマタイプ",
                "brandId" to "30018",
                "categoryId" to "20002",
                "description" to "\"5つの高級美容オイルを配合し、メイクを落とすだけではなくエイジングケア※1をしながら肌を明るく導く、大人のクレンジングオイル。\n" +
                        "くすみ※2の原因となる肌ステイン※3を、珊瑚草オイルとロックローズオイルが除去。濡れた手でも使えるウォーターキャッチオイル処方、素早い洗い流しができる微細乳化処方。\n" +
                        "スピーディメルティング処方がフィット感の高いファンデーション、ウォータープルーフなどの落ちにくいメイクも瞬時に浮き上がらせます。W洗顔不要、まつげエクステンションにも使用できます。\n" +
                        "\n" +
                        "※1 年齢に応じたお手入れ\n" +
                        "※2 古い角質の汚れのこと\n" +
                        "※3 古い角質の汚れのこと\"",
                "ingredientIDs" to listOf(
                    "40088",
                    "40162",
                    "40160",
                    "40324",
                    "40095",
                    "40135",
                    "40053",
                    "40173",
                    "40329",
                    "40061"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "スピーディーマスカラリムーバー",
                "brandId" to "30060",
                "categoryId" to "20002",
                "description" to "\"落ちにくいウォータープルーフマスカラも塗るだけですばやくなじみ、溶けるようにするんと落とすマスカラ用リムーバー。\n" +
                        "ゴシゴシせずに落とせるので、まつ毛とまぶたに負担をかけにくいリムーバーです。\n" +
                        "まつ毛ケア成分(※1)配合で、傷みやすいまつ毛にうるおいを与えます。\n" +
                        "また、目にしみにくいので、まつ毛にしっかりとなじませられます。\n" +
                        "皮フ刺激テスト済み(※3)。\"",
                "ingredientIDs" to listOf(
                    "40076",
                    "40253",
                    "40072",
                    "40360",
                    "40010",
                    "40043",
                    "40229",
                    "40061",
                    "40115",
                    "40100"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "マイルドクレンジングオイル 60ml",
                "brandId" to "30061",
                "categoryId" to "20002",
                "description" to "\"うるおい守って毛穴つるん♪\n" +
                        "新「マイクレ」ですっぴんが変わる!*1\n" +
                        "\n" +
                        "@cosmeベストコスメアワード2020 殿堂入りを果たした『マイルドクレンジング オイル』がリニューアル。肌への負担を抑えて簡単にメイクを落とせるだけでなく、素肌そのものもキレイにする\"\"毛穴レス*2スキンケアクレンジング\"\"へと進化しました。\n" +
                        "まつ毛エクステOK*3、濡れた手でも使えます。\n" +
                        "*1 洗浄効果による\n" +
                        "*2 汚れを落とし、毛穴が目立ちにくいこと\n" +
                        "*3 一般的なグルー(シアノアクリレート系)を使用したまつげエクステンションをご使用の方もお使いいただけます。\"",
                "ingredientIDs" to listOf(
                    "40088",
                    "40162",
                    "40160",
                    "40324",
                    "40095",
                    "40135",
                    "40256",
                    "40166",
                    "40377",
                    "40180"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アルティム8∞ スブリム ビューティ クレンジング オイル",
                "brandId" to "30043",
                "categoryId" to "20002",
                "description" to "\"シュウ ウエムラ史上、最高傑作。\n" +
                        "伝説*1のクレンジング オイル「アルティム8」が8つの美肌効果と共に生まれ変わる。\n" +
                        "ベストコスメ65冠*2を受賞した人気クレンジング オイルが、独自の先進テクノロジーを採用し、クレンジング効果、生分解性において進化を遂げました。椿オイル*3に加えて新たに椿の花びらエキス*4を配合し、洗い上がりの肌のうるおい感が続きます。洗うたび感じる、肌の透明感*5、ハリ。キメが整い、毛穴も目立たない*5なめらかで輝く肌へ。\n" +
                        "\n" +
                        "1.独自の先進クレンジング テクノロジーを搭載\n" +
                        "優れた洗浄力はそのままに、洗い流した後の環境のことを考慮して独自のテクノロジーを搭載。99%生分解性処方*6に進化しました。\n" +
                        "\n" +
                        "2.椿オイル*3に加えて、新たに椿の花びらエキス*4配合\n" +
                        "長年、使用しているツバキ種子に加え、椿の花びらエキスがもたらす美肌効果に着目&配合。また、種子に加えて花びらまで余すことなく使用することで花びらの廃棄を大幅に削減するなど、サステナブルなフォーミュラになりました。\n" +
                        "\n" +
                        "3.「アルティム8」の8つの美肌効果\n" +
                        "洗い上がりの肌のうるおい感が続くクレンジング オイル。洗うたび感じる、肌の透明感*5、ハリ。キメが整い、毛穴も目立たない*5なめらかで輝く肌へ導きます。\n" +
                        "\n" +
                        "<8つの美肌効果>\n" +
                        "クレンジングによる肌の透明感・毛穴の目立ち・ハリ・輝き・うるおい感・キメ・なめらかさ・肌を健やかに保つ\n" +
                        "\n" +
                        "*1 シュウ ウエムラにおいて\n" +
                        "*2 雑誌・WEB等のメディアにおける2020年4月～2023年6月迄のベストコスメ相当賞受賞総数\n" +
                        "*3 ツバキ種子油 (整肌成分)\n" +
                        "*4 ツバキ花エキス(整肌成分)\n" +
                        "*5 クレンジングによる\n" +
                        "*6 OECD 301、もしくは同等の方法による\"",
                "ingredientIDs" to listOf(
                    "40242",
                    "40288",
                    "40364",
                    "40165",
                    "40169",
                    "40107",
                    "40377",
                    "40315",
                    "40403",
                    "40191"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/1.jpg"
            ),
            mapOf<String, Any>(
                "name" to "うる落ち水クレンジングローション アイメイクアップリムーバー",
                "brandId" to "30058",
                "categoryId" to "20002",
                "description" to "\"デリケートなまつげや目元、口元に。\n" +
                        "うるおいを守りながら、ウォータープルーフマスカラまでするんと落とす、ポイントメイクリムーバーです。\n" +
                        "化粧水由来の洗浄成分を含んだ水層と油性エモリエント層のWの効果で肌のうるおいを守りながら、ウォータープルーフマスカラやアイライナー、口紅もするんと落とします。\n" +
                        "ビタミンB・E誘導体配合でデリケートなまつげや目元、口元を保湿・保護。\n" +
                        "\n" +
                        "無香料・無着色・防腐剤フリー・低刺激処方。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40171",
                    "40360",
                    "40072",
                    "40085",
                    "40027",
                    "40415",
                    "40150",
                    "40017",
                    "40297"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ベーシッククリーム",
                "brandId" to "30042",
                "categoryId" to "20002",
                "description" to "\"低刺激性、天然オイル中心のウォッシングタイプのクレンジングです。メイクやお肌の汚れをしっかり落とし、しっとりなめらかに保ちます。\n" +
                        "また、マッサージクリームとしても使用できます。\n" +
                        "【使用量の目安:4cm位】\n" +
                        "\n" +
                        "使い方\n" +
                        "\n" +
                        "■ダブル洗顔(クレンジング+洗顔フォーム)のご使用方法\n" +
                        "\n" +
                        "1.クレンジングを4～5cmほど手にとって、顔全体にのばします。気温が低いときは人肌程度に温めて使用すると、肌によくなじんで汚れが浮き立ちます。\n" +
                        "2.洗顔は手早くこすらないようにすると、ほとんどの汚れが落ちます。洗顔のみだと約50%、水洗いだと約20～30%の汚れしか落ちません。\"",
                "ingredientIDs" to listOf(
                    "40109",
                    "40191",
                    "40010",
                    "40025",
                    "40193",
                    "40237",
                    "40328",
                    "40197",
                    "40327",
                    "40105"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ポアクリア オイル",
                "brandId" to "30039",
                "categoryId" to "20002",
                "description" to "\"毛穴角栓を溶解!\n" +
                        "うるおいを守りながら毛穴の奥までマイクロ洗浄\n" +
                        "\n" +
                        "コーセー独自の技術力を結集する高効能ブランド、ONE BY KOSÉが提案するクレンジングオイル!\n" +
                        "どんなにケアしてもくり返す、毛穴の詰まり・黒ずみに、毎日のクレンジングオイルで手軽にアプローチ。毛穴の約20,000 分の1 のサイズの微細な洗浄成分を配合し、固まり角栓をほぐして溶解します。\n" +
                        "厳選したオイルと独自の技術力で、毛穴が目立たないクリアな肌に整えます。\"",
                "ingredientIDs" to listOf(
                    "40360",
                    "40169",
                    "40288",
                    "40452",
                    "40168",
                    "40073",
                    "40444",
                    "40451",
                    "40165",
                    "40234"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "サンシビオ エイチツーオー D",
                "brandId" to "30057",
                "categoryId" to "20002",
                "description" to "\"フランス発☆ビオデルマサンシビオH2Oは、\n" +
                        "デリケートなお肌のための、洗い流し不要のクレンジングウォーターです。\n" +
                        "お肌に刺激を与えずに、軽くなじませるだけで\n" +
                        "ウォータープルーフのアイメイクなどの落ちにくいメイクにもきちんとなじみます。\n" +
                        "メイクを落とすだけでなく角質層の水分バランスを整え、お肌を落ち着かせてしっとりとさせます。\n" +
                        "洗い流し不要なのでオフィスや機内でのメイク直しにも便利です。\n" +
                        "\n" +
                        "使い方\n" +
                        "\n" +
                        "コットンにたっぷり含ませ、\n" +
                        "ファンデーションなどのフェイスメイクやアイメイクとよくなじませてこすらずやさしくふき取っていきます。\n" +
                        "そのまま洗い流さずにスキンケアの次ぎのステップへお進みいただけます。(お好みに応じて洗い流してください)\n" +
                        "※アイメイクを落とす際は、特に目に入らないようにご注意ください。\"",
                "ingredientIDs" to listOf(
                    "40029",
                    "40017",
                    "40211",
                    "",
                    "40036",
                    "40130",
                    "40318",
                    "40359",
                    "40127",
                    "40402"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ザ クレンジングバーム ブラックリペア",
                "brandId" to "30052",
                "categoryId" to "20002",
                "description" to "\"男女を問わず、毛穴の黒ずみやテカリは、肌悩みの上位。\n" +
                        "独自開発の炭*×発酵の 力を掛け合わせた黒いクレンジングバームが、やさしくうるおいながら、黒ずみ や過剰皮脂をしっかり除去。\n" +
                        "毛穴やザラつきが気になる女性や、皮脂・テカリが 気になる男性にもご使用いただけます。\n" +
                        "\n" +
                        "*吸着成分\n" +
                        "\n" +
                        "使い方\n" +
                        "\n" +
                        "【使用量】専用スパチュラにさくらんぼ大をすくってお使いください。\n" +
                        "手肌が乾いた状態で、適量(専用スパチュラにさくらんぼ大)を手に取り、顔の中心から外、下から上へ全体になじませます。\n" +
                        "小鼻、目元、口元などの汚れが溜まりやすい部分は指の腹を使って優しくクルクルと円を描くように。\n" +
                        "その後、ぬるま湯で20～30回程度ていねいに洗い流してください。W洗顔は不要です。\"",
                "ingredientIDs" to listOf(
                    "40251",
                    "40432",
                    "40073",
                    "40069",
                    "40312",
                    "40139",
                    "40061",
                    "40443",
                    "40295",
                    "40459"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "フレッシュ クリア サクラ クレンジング オイル",
                "brandId" to "30043",
                "categoryId" to "20002",
                "description" to "\"■特徴\n" +
                        "1）さらっとしたテクスチャーでしっかりメイクも一度でオフする99%自然由来のフォーミュラ。\n" +
                        "2）細かい分子のオイルを使用した高い洗浄力と毛穴悩みへのアプローチ。\n" +
                        "3）使い続けるほどに、フレッシュでなめらかな肌へ。\n" +
                        "\n" +
                        "■フレグランス\n" +
                        "サクラのフレーバーティーのような、ナチュラルでリラックスと清涼感をもたらすフレグランス。\"",
                "ingredientIDs" to listOf(
                    "40364",
                    "40169",
                    "40165",
                    "40107",
                    "40315",
                    "40444",
                    "40244",
                    "40010",
                    "40176",
                    "40403"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "メイク キープ ミスト EX",
                "brandId" to "30040",
                "categoryId" to "20003",
                "description" to "\"メイクの仕上げに細かなミストをシュッと吹きかけるだけで化粧崩れしにくくなる、ふんわりミストスプレー。\n" +
                        "\n" +
                        "■つけたての美しいメイクを長時間キープ!\n" +
                        "メイクコート成分配合。しっかりとメイクを固定しながら、顔の動きに合わせて伸縮するため、時間がたってもメイクがヨレることなく、美しい仕上がりをキープします。\n" +
                        "\n" +
                        "■汗・皮脂プルーフ成分配合\n" +
                        "汗や水、皮脂まではじくため、メイクによるテカリやにじみをしっかりと抑えます。\n" +
                        "\n" +
                        "■乾燥を防ぎ、うるおいもキープ。\n" +
                        "保湿成分配合。うるおいを与え、乾燥から肌をガード。みずみずしいうるおいが続きます。\n" +
                        "\n" +
                        "■フレッシュフローラルの香り\n" +
                        "\n" +
                        "■無着色\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40172",
                    "40085",
                    "40183",
                    "40260",
                    "40010",
                    "40352",
                    "40051",
                    "40298",
                    "40387"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リポソーム アドバンスト リペアセラム",
                "brandId" to "30041",
                "categoryId" to "20003",
                "description" to "\"●生体組成成分リン脂質から成り、たまねぎ状に幾重にも重なる層の中に美容成分を贅沢に抱えた0.1ミクロン※1の超微細なマイクロカプセル「新・多重層バイオリポソーム」が、1滴に1兆個※2。つけた瞬間から、成りかわるように肌に溶け込み、カプセルそのものがダイレクトに肌を美しくすることで、潤いに満ちたハリ・ツヤあふれる若々しい印象に導く、新・リポソーム美容液です。\n" +
                        "●角層深くまで浸透し、ぴったりと密着。カプセルが少しずつほぐれるように、じっくりと長時間、潤いと美容成分を届けます。\n" +
                        "●乾燥小ジワを目立たなくし※3、乱れたキメも美しく整え、毛穴レスな、なめらかな肌へ。\n" +
                        "●長時間潤いが持続。低湿度の過酷な環境下でも潤いを保持し、あらゆる肌不調を未然に防ぎ、乾燥などの外的ストレスに負けない健やかな肌に導きます。\n" +
                        "●後から使う化粧品のなじみを良くするブースティング効果があります。\n" +
                        "●リッチな保湿感がありながらも、べたつかない、みずみずしいオイルフリー処方。\n" +
                        "●乾燥肌にも深く優しくなじむ、低刺激処方です。\n" +
                        "●肌と心を癒すような、繊細で心地よいティーグリーンフローラルの香り。穏やかな気持ちから前向きな気持ちへと導かれるような変化を楽しめます。\n" +
                        "\n" +
                        "※1 0.1ミクロン台の粒径\n" +
                        "※2 1滴0.1mLとして算出(概算値)\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40135",
                    "40454",
                    "40016",
                    "40022",
                    "40050",
                    "40084",
                    "40186",
                    "40215"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ザ・タイムR アクア",
                "brandId" to "30024",
                "categoryId" to "20003",
                "description" to "\"肌表面をたっぷりのうるおいで満たし、キメを整える薬用化粧水。\n" +
                        "\n" +
                        "うるおい成分を抱えた水*1の層を肌表面につくり、キメを整え、ぷるぷると水*1をまとったようなみずみずしい感触を持続させる薬用化粧水。\n" +
                        "数々の@cosmeベストコスメアワードを受賞していて、すでにファンも多い人気の高い化粧水で、メンズファッション誌*2など、多数のコスメアワードを受賞しています。\n" +
                        "\n" +
                        "たっぷり水分補給へ導くイプサ独自のメカニズム\n" +
                        "イプサ独自の保湿成分「アクアプレゼンターIII」が、肌表面にうるおい成分を留まらせる人工的な水*1の層をつくり、乾燥状態に応じて水分を与えるとともに逃さないようにするので、肌*3が水*1で満たされた状態が持続します。乾燥、テカリ、大人ニキビ、肌荒れなどの肌トラブルを防ぎます。\n" +
                        "\n" +
                        "アルコールフリー・油分フリー\n" +
                        "乾燥、大人のニキビ、テカリ、肌荒れ…などどんな肌状態でも、アルコールフリー・油分フリーだからやさしく使用でき、ベタつかずにしっかり浸透*3して、うるおいが持続します。\n" +
                        "\n" +
                        "洗練されたシンプルなデザインで性別問わず人気\n" +
                        "心もうるおう湧き上がるような水を感じるフォルム、ふれたときの心地よさを兼ね備えたデザイン。シンプルで個性的、インテリアにも溶け込むスタイリッシュなボトルは性別を問わず幅広い人気があります。\n" +
                        "\n" +
                        "基本の使い方\n" +
                        "(1)朝と夜の洗顔後、コットンまたは手のひらに500円硬貨大よりやや大きめにたっぷり(約2ml)とります。\n" +
                        "(2)肌全体に均一になじませます。\n" +
                        "(3)コットンをご利用の場合は、手首を使って風をおくるように手を大きくリズミカルにパッティングします。毛穴の目立ちが気になる部分や、皮脂分泌量の多いTゾーンは念入りにパッティングします。\n" +
                        "※クリームの前など、スキンケアのどの順序に組み入れても使えます。\n" +
                        "\n" +
                        "*1 うるおいに対するイプサのイメージ表現\n" +
                        "*2 MEN‘S NON-NO 2021年美容大賞 殿堂入り\n" +
                        "*3 角層まで\"",
                "ingredientIDs" to listOf(
                    "40247",
                    "40138",
                    "40052",
                    "40015",
                    "40185",
                    "40457",
                    "40182",
                    "40467",
                    "40218",
                    "40093"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ブライトニング ローション WT II",
                "brandId" to "30031",
                "categoryId" to "20003",
                "description" to "\"透明感とハリに満ちた「つや玉」続く肌へ 美白*&エイジングケア**の化粧水\n" +
                        "\n" +
                        "うるおいや美容成分をきちんと届ける薬用美白化粧水(しっとりタイプ)。\n" +
                        "美白有効成分配合のブライトリフレクト処方で、うるおいに満ちた、光をきれいに反射する肌に整え、透明感を与えます。さらに、美容成分配合で、肌にふっくらとしたハリを与えます。\n" +
                        "\n" +
                        "*メラニンの生成を抑え、シミ・そばかすを防ぐ。\n" +
                        "**年齢に応じたうるおいケア。\n" +
                        "(医薬部外品)\"",
                "ingredientIDs" to listOf(
                    "40138",
                    "40101",
                    "40381",
                    "40421",
                    "40106",
                    "40019",
                    "40103",
                    "40080",
                    "40455",
                    "40457"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "タカミスキンピール",
                "brandId" to "30048",
                "categoryId" to "20003",
                "description" to "\"タカミの原点。美肌サイクルに着目した“角質美容水”\n" +
                        "\n" +
                        "肌表面に一時的に働きかけるのではなく、美肌のカギをにぎる角質層に直接アプローチし、肌本来の美しさを保ちます。角質層を整えることで、正しい肌の生まれ変わり(ターンオーバー)をサポート。内側*1から透明感*2あふれるような柔らかな肌に導きます。毎日使うたびに、より実感いただけます。\n" +
                        "\n" +
                        "*1 角質層まで *2 うるおいを与えることによる肌印象\n" +
                        "\n" +
                        "美しさの原点は、毎日生まれ変わる肌に寄り添うスキンケア\n" +
                        "美しさが邪魔されていると感じたら、まず疑って欲しいのは、肌のターンオーバーが乱れていること。\n" +
                        "乾燥やハリ不足など肌表面に見えている悩みに追われていると、美しさの根源ともいえるターンオーバーのサポートを忘れてしまいがちです。ターンオーバーと向き合いサポートすることは、どんなときもゆらぎにくい、透明感*2と生命力のある美しい肌を目指すことに繋がるのです。\n" +
                        "\n" +
                        "*2 うるおいを与えることによる肌印象\n" +
                        "\n" +
                        "肌表面を剥がすピーリングではありません。こだわりの配合バランス\n" +
                        "「スキンピール」という名前から誤解されがちですが、肌表面を剥がすものではありません。肌にやさしい方法で、すこやかな美しさを保つ処方を追求し続けました。\n" +
                        "美容皮膚の現場で20万人以上*3の肌と向き合い、導き出したタカミ独自の処方です。\n" +
                        "\n" +
                        "*3 2008年1月～2016年8月の延べ人数\n" +
                        "\n" +
                        "年齢や肌質に関わらず、誰でも手軽に毎日できる角質美容水\n" +
                        "洗顔と化粧水の間にプラスワンするだけ。いつものスキンケアにタカミスキンピールを取り入れる「スキンピール習慣」が、あなたの美しさを毎日丁寧に積み上げていきます。水のようなテクスチャーは他スキンケアの使用感を妨げず、快適な使い心地です。\n" +
                        "\n" +
                        "【こんな方にオススメ】\n" +
                        "・毛穴の黒ずみ、詰まりが気になる方\n" +
                        "・くすみ*4、透明感*2がない、テカリ、乾燥、ハリ不足が気になる方\n" +
                        "\n" +
                        "*2 うるおいを与えることによる肌印象 *4 乾燥しキメが乱れたことによる肌印象\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40331",
                    "40022",
                    "40158",
                    "40270",
                    "40143",
                    "40404",
                    "40110",
                    "40389"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ルルルン ハイドラ V マスク",
                "brandId" to "30073",
                "categoryId" to "20004",
                "description" to "\"毛穴が気にならない、うるおいたっぷりの水光\n" +
                        "肌を素早くケア、集中メンテナンス\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40010",
                    "40047",
                    "40361",
                    "40317",
                    "40280",
                    "40291",
                    "40268",
                    "40297"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "スーパーVC100マスク ",
                "brandId" to "30047",
                "categoryId" to "20004",
                "description" to "\"毛穴やざらつき、年齢とともに気になるくすみ※1にもアプローチ。肌に潤いを与えて、ハリとクリアな肌に導きます。\n" +
                        "\n" +
                        "■4種の濃厚ビタミン C(※4) 配合\n" +
                        "肌への浸透(※6)性にすぐれた APPS(パルミチン酸アスコルビルリン酸 3Na(※4))、アスコルビン酸(※4)、テトラヘキシルデカン酸アスコルビル(※4)、リン酸アスコルビルMg(※4)を配合。\n" +
                        "\n" +
                        "■ナノカプセルテクノロジー採用 　\n" +
                        "肌になじみやすい水添レシチン(※5)のカプセルが、角質層に浸透し肌に潤いを与えます。\n" +
                        "\n" +
                        "■シリコンマスク発想の高密着(※7)・高(※7)浸透(※6)Premium シート\n" +
                        "コットン生まれの長繊維不織布をシートに採用。美容液をたっぷりと含んだ密着性の高い(※7)シートが肌にピタッとフィットし、浸透(※6)をサポートします。\n" +
                        "\n" +
                        "※1 乾燥によりくすんで見えるお肌に潤いを与え明るい印象に導く　\n" +
                        "※2 肌をやわらげることによりお肌に弾力を持てる　\n" +
                        "※3 明るく透明感のあるお肌に整える　\n" +
                        "※4 パルミチン酸アスコルビルリン酸3Na、アスコルビン酸、テトラヘキシルデカン酸アスコルビル、リン酸アスコルビルMg(全て酸化防止剤)　\n" +
                        "※5 保湿　\n" +
                        "※6 角質層まで　\n" +
                        "※7 クオリティファースト内\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40016",
                    "40135",
                    "40268",
                    "40048",
                    "40286",
                    "40408",
                    "40238",
                    "40137",
                    "40010"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/2.jpg"
            ),
            mapOf<String, Any>(
                "name" to "シカデイリースージングマスク",
                "brandId" to "30014",
                "categoryId" to "20004",
                "description" to "\"乾燥した肌に素早く潤いチャ―ジしてくれるシカデイリースージングマスク。\n" +
                        "0.2mmの薄さのヌードシールシートなので肌が息苦しくなく、お肌に優しい作りになっております。\n" +
                        "ピンセットが付属で入っておりますので衛生面を気にせず安心してお使いいただけます。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40016",
                    "40006",
                    "40118",
                    "40322",
                    "40116",
                    "40220",
                    "40265",
                    "40144"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "3番すべすべキメケアシートマスク",
                "brandId" to "30007",
                "categoryId" to "20004",
                "description" to "高価な発酵成分と毛穴収れん成分を配合し、たった1回使うだけで、肌のキメの変化が実感できる集中キメケアシートマスク",
                "ingredientIDs" to listOf(
                    "40444",
                    "40310",
                    "40121",
                    "40371",
                    "40136",
                    "40135",
                    "40268",
                    "40033",
                    "40006",
                    "40205"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "レチビタエッセンスマスク",
                "brandId" to "30059",
                "categoryId" to "20004",
                "description" to "\"浸透型ビタミンC誘導体とレチノール誘導体*1を配合し、毛穴の目立ちにアプローチし、贅沢なデイリーケアを実現。\n" +
                        "ピュレア独自の10種類の発酵コンプレックス成分が角質層のバリア機能をサポートします。\n" +
                        "*1 整肌成分\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40322",
                    "40006",
                    "40268",
                    "40137",
                    "40265",
                    "40326",
                    "40060",
                    "40300"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ディオール アディクト リップ マキシマイザー ",
                "brandId" to "30050",
                "categoryId" to "20005",
                "description" to "\"うるおいとふっくらボリュームアップ(*2)をもたらす大人気のリップ プランパー、ディオール アディクト リップ マキシマイザーが、クールなセンセーションはそのままに、自然由来成分(*1)の使用にこだわったフォーミュラに進化して登場します。\n" +
                        "チェリー オイル(*3)とヒアルロン酸(*4)をたっぷりと配合した処方は、90%(*1)が自然由来成分で構成。\n" +
                        "1日中続く潤いと、あふれる輝きを得て、滑らかでふっくらとボリュームアップ(*2)した唇を叶えます。\n" +
                        "\n" +
                        "新しいディオール アディクト リップ マキシマイザーは、透明感を際立たせるナチュラルなカラーから鮮やかに発色する魅力溢れるシェードまで幅広く揃え、あらゆるメイクアップのリクエストに応えます。仕上がりのバリエーションも、新たに4つのフィニッシュで登場。\n" +
                        "・クラシック:#001に代表される、初代から存在する「ザ・クラシック」。みずみずしい輝き。\n" +
                        "・シマー:繊細で透明感のある輝き。\n" +
                        "・ホログラフィック:マイクログリッターの効果で、煌めく輝き。\n" +
                        "・インテンス:鮮やかな発色とフレッシュさあふれる輝き。\n" +
                        "\n" +
                        "新生リップ マキシマイザーのケースには、シルバーに煌めく「ディオール オブリーク」のシグネチャーがあしらわれ、ジュエリーのような華やかなクチュール デザインにアップグレード。\n" +
                        "\n" +
                        "(*1)自然由来指数90%(水0%を含む)ISO16128準拠\n" +
                        "(*2)メイクアップ効果\n" +
                        "(*3)セイヨウミザクラ種子油(保湿成分)\n" +
                        "(*4)ヒアルロン酸Na(保湿成分)\n" +
                        "\n" +
                        "使い方\n" +
                        "\n" +
                        "1. 単独、もしくはリップバームやリップスティックの上に使用するとミラーのようなツヤと輝きをもたらすグロス エフェクトが生まれます。\n" +
                        "2. 口紅のベースとして使用する際は、余分なグロスを拭き取ってから口紅を塗布してください。潤いのある滑らかな唇に仕上がります。\n" +
                        "3. ディオール アディクト リップ マキシマイザー セラムと組み合わせれば、集中ケアを叶えます。\n" +
                        "4.ディオール アディクト リップ マキシマイザー #001は就寝前のナイトケアとしてもご使用いただけます。\"",
                "ingredientIDs" to listOf(
                    "40351",
                    "40406",
                    "40360",
                    "40262",
                    "40288",
                    "40235",
                    "40184",
                    "40177",
                    "40383",
                    "40428"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "エッセンス リッププランパー",
                "brandId" to "30025",
                "categoryId" to "20005",
                "description" to "じんじんとした刺激感&心地よい清涼感でぷっくりボリュームのある唇を叶え、乾燥を防ぐリッププランパー。",
                "ingredientIDs" to listOf(
                    "40263",
                    "40406",
                    "40451",
                    "40351",
                    "40234",
                    "40225",
                    "40184",
                    "40290",
                    "40244",
                    "40298"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ラッシュジェリードロップ EX",
                "brandId" to "30065",
                "categoryId" to "20005",
                "description" to "\"マジョリカ マジョルカ ラッシュジェリードロップは、\n" +
                        "傷んだキューティクルを1本1本トリートメントして、\n" +
                        "つやとハリを与えるまつ毛用美容液です。\n" +
                        "\n" +
                        "たっぷり液を含み、まつ毛の根もとからフィットさせ、\n" +
                        "まつ毛の際にも塗りやすい、デュウドロップ型のチップを採用。\n" +
                        "まつ毛の生え際に、うるおいを与えます。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40016",
                    "40085",
                    "40135",
                    "40062",
                    "40270",
                    "40356",
                    "40114",
                    "40308"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リポソーム アドバンスト リペアアイセラム",
                "brandId" to "30041",
                "categoryId" to "20005",
                "description" to "\"高濃度ダブルカプセル1.6兆個※2 24時間 いつでも速攻ケア 潤い補給 エイジング「多重層バイオリポソーム」×「新開発ナノバイセル」\n" +
                        "乾燥小ジワやハリのなさなどのエイジングサインが出やすい目もとをかけめぐり、潤いを張り巡らせ、朝も夜も、輝くように若々しい印象へ。日々酷使される目もとを見つめ抜いたアイセラム。\n" +
                        "\n" +
                        "●角層が薄くバリア機能が低い目もとの皮膚特性を見つめ、肌にもともと存在するバリア成分であるヒト型セラミドを閉じ込めた、0.02～0.07ミクロンのディスク状単層カプセル\n" +
                        "「ナノバイセル」を新開発。たまねぎ状に幾重にも重なる層の中に美容成分を贅沢に抱えた0.1ミクロン※1の球状多重層カプセル「多重層バイオリポソーム」と「ナノバイセル」を配合した\n" +
                        "ダブルカプセル処方の高機能目もと用美容液です。\n" +
                        "●「ナノバイセル」も「多重層バイオリポソーム」も、ともに生体組成成分リン脂質から成る肌なじみに優れたカプセルです。この2種の美肌カプセルが1滴に1.6兆個※2。\n" +
                        "角層深く浸透し、成りかわるように肌に溶けこみ、潤いに満ちたハリ・ツヤ・明るさあふれる若々しい目もと印象に導きます。\n" +
                        "●乾燥によるくすみ、キメの乱れ、ハリのなさ、乾燥小ジワなど、疲れた目もと印象にアプローチ。乾燥小ジワを目立たなくし※3、キメ細かく、ハリのあるなめらかな肌へ。\n" +
                        "●24時間いつでも潤いケアすることで乾く隙を与えず、乾燥による目もとトラブルを寄せつけません。\n" +
                        "●デリケートな目もとに負担感なくのび広がりすっとなじむ、オイルフリーのみずみずしいエッセンスベース。季節や朝・夜問わず使用できます。\n" +
                        "●乾燥肌にも深く優しくなじむ、低刺激処方です。\n" +
                        "●肌と心を癒すような、繊細で心地よいティーグリーンフローラルの香り。(リポソームシリーズ共通)\n" +
                        "●金属製のキャップ上部の丸い部分はマッサージャーとして、ツボ押しに使用可能です。マッサージにより、心地よく目元の疲れをほぐします。\n" +
                        "\n" +
                        "※1 0.1ミクロン台の粒径\n" +
                        "※2 1滴0.1mlとして算出(概算値)\n" +
                        "※3 効能評価試験済み\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40135",
                    "40085",
                    "40016",
                    "40454",
                    "40054",
                    "40063",
                    "40084",
                    "40098"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "グラスティングメルティングバーム",
                "brandId" to "30010",
                "categoryId" to "20005",
                "description" to "\"なめらかで軽い塗り心地でもっちり潤う水膜リップバーム\n" +
                        "鮮やかで多彩なカラー構成で植物性保湿オイルが唇をしっとりもっちりと保湿してくれます。\n" +
                        "\n" +
                        "なめらかなタッチで唇にぴったりフィットします。\n" +
                        "水分が飛ばない比率が塗り重ねても透明感のあるリップバームを演出します。\n" +
                        "\n" +
                        "やわらかな血色感あるアプリコットベージュ\"",
                "ingredientIDs" to listOf(
                    "40406",
                    "40252",
                    "40303",
                    "40284",
                    "40263",
                    "40235",
                    "40354",
                    "40070",
                    "40006",
                    "40174"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ブライトニング スキンケアパウダー A ",
                "brandId" to "30045",
                "categoryId" to "20006",
                "description" to "\"美白*ケア&透明感アップ(メイクアップ効果による)。薬用美白*スキンケアパウダー 詰め替え用レフィル。\n" +
                        "\n" +
                        "朝はメイクのしあげや日中は化粧なおし、夜はスキンケアの最後の素肌ケアに。\n" +
                        "美白有効成分「4MSK」(4-メトキシサリチル酸カリウム塩)配合で、メラニンの生成を抑え、シミ・そばかすを防ぐ。\n" +
                        "穏やかにやさしく香る、フローラルアロマの香り。\n" +
                        "\n" +
                        "「雪の精のことば」デザインフィル付き。\n" +
                        "*コンパクトの中のデザインフィルムは全部で8種類。どれが出るかはお楽しみ(デザインはお選びいただけません)。\n" +
                        "\n" +
                        "美白* メラニンの生成を抑え、しみ・そばかすを防ぐ\"",
                "ingredientIDs" to listOf(
                    "40009",
                    "40139",
                    "40273",
                    "40159",
                    "40052",
                    "40365",
                    "40467",
                    "40226",
                    "40214",
                    "40336"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "【レフィル】スキンケアUV タッチアップ クッション",
                "brandId" to "30017",
                "categoryId" to "20006",
                "description" to "\"いつでもどこでもUVケアとうるおい補給を。\n" +
                        "メイクアップの上から日やけ止めを塗り直せるクッションコンパクト。\n" +
                        "SPF 45 PA+++で紫外線から肌を守りながら、スキンケア感覚でタッチアップ。\n" +
                        "美容液 90%*配合、10種のオーガニック植物由来成分配合でうるおいを与えます。\n" +
                        "*粉体を除くエマルション\n" +
                        "\n" +
                        "[製品特長]\n" +
                        "■ みずみずしい使用感で、スキンケアしたてのようなツヤのある仕上がりに\n" +
                        "■ 浸透性美容液成分で保湿成分を浸透しやすくし、モイストバーム成分で うるおいキープ\n" +
                        "■ 002と003はパール配合で肌を補正しトーンアップ\n" +
                        "■ メイクアップの上から使っても素早くなじみ、よれにくい\n" +
                        "■ 乾燥や日焼けが気になる時に。プライマーとしても使用可能\n" +
                        "■ 大気中のちりやほこりなどの微粒子から肌を守ります\n" +
                        "■ ヴィーガンフレンドリー、グルテン・鉱物油フリー\n" +
                        "■ 無香料\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40172",
                    "40378",
                    "40372",
                    "40085",
                    "40073",
                    "40034",
                    "40168",
                    "40376",
                    "40135"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "スキンケアパウダー",
                "brandId" to "30024",
                "categoryId" to "20006",
                "description" to "\"寝ている間もスキンケアを封じ込める美白パウダー。\n" +
                        "夜のお手入れの最後にさっとひと塗りするだけで、睡眠中もすこやかな肌へ。\n" +
                        "素肌で過ごしたい日もスキンケアの最後にお使いいただくことでいつでも快適さらさらすべすべ肌が続きます。\n" +
                        "パフ付き\n" +
                        "\n" +
                        "ノンコメドジェニックテスト済み\n" +
                        "※全てのかたにニキビができないというわけではありません。\n" +
                        "○4MSK(4-メトキシサリチル酸カリウム塩)(美白有効成分)配合\n" +
                        "※メラニンの生成を抑え、シミ・ソバカスを防ぐ\n" +
                        "○酢酸DLーαートコフェロール(肌あれ防止有効成分)配合\n" +
                        "○肌あれ防止スキンケアパウダー(硫酸バリウム)配合\n" +
                        "○Sヒアルロン酸(アセチル化ヒアルロン酸)(保湿成分)配合\"",
                "ingredientIDs" to listOf(
                    "40009",
                    "40441",
                    "40226",
                    "40375",
                    "40336",
                    "40122",
                    "40021",
                    "40202",
                    "40374",
                    "40195"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ホワイトルーセント ブライトニング スキンケアパウダー N",
                "brandId" to "30011",
                "categoryId" to "20006",
                "description" to "\"朝も夜もスキンケアの仕上げに。美白有効成分m-トラネキサム酸**を配合した薬用美白美容パウダー。　\n" +
                        "べたつかないさらさら感触で、透明感のあるつや肌を演出します。　\n" +
                        "肌も心も明るくなるようなリラックス感のある香り。\n" +
                        "\n" +
                        "*美白とはメラニンの生成を抑え、シミ・そばかすを防ぐこと 　\n" +
                        "**トラネキサム酸\"",
                "ingredientIDs" to listOf(
                    "40247",
                    "40336",
                    "40214",
                    "40226",
                    "40375",
                    "40433",
                    "40021",
                    "40374",
                    "40195",
                    "40416"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "SHISEIDO(シセイドウ)ベビーパウダー(プレスド)",
                "brandId" to "30011",
                "categoryId" to "20006",
                "description" to "\"あせも、ただれ、おむつかぶれ、股ずれを防ぎ、赤ちゃんの肌をすこやかに保ちます。\n" +
                        "パウダーの飛び散りが少ない、固形タイプなので、携帯にも便利。\"",
                "ingredientIDs" to listOf(
                    "40435",
                    "40226",
                    "40340",
                    "40471",
                    "40411",
                    "40457",
                    "40131",
                    "40409",
                    "40428",
                    ""
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "サンシェルター マルチ プロテクション トーンアップCC",
                "brandId" to "30041",
                "categoryId" to "20007",
                "description" to "\"心地よいスキンケアタッチで透明感を守り抜く エッセンスクリームベースで美しい素肌感をひき出す、ナチュラルカバーのトーンアップCC。\n" +
                        "【01ライトベージュ】肌なじみのよいライトベージュ\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40172",
                    "40378",
                    "40085",
                    "40183",
                    "40034",
                    "40163",
                    "40289",
                    "40372",
                    "40226"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ニベアUV ディープ プロテクト&ケア ジェル",
                "brandId" to "30054",
                "categoryId" to "20007",
                "description" to "\"ニベア最強*1シリーズ!美容ケアもできる日やけによるシミ予防*2UV\n" +
                        "\n" +
                        "まるで美容液のようなうるおいUV、誕生。欲張りを叶える「美容ケアUV」で、ポジティブな夏肌へ!\n" +
                        "◆SPF50+/PA++++\n" +
                        "◆スーパーウォータープルーフ*:汗・水に強い3次元UVフィルムを採用。*ブランド内において\n" +
                        "◆せっけんで落とせる\n" +
                        "◆全身にたっぷり使えるジェル。化粧下地にも使えるエッセンス。\n" +
                        "◆さわやかで透明感あるクリアフローラルの香り\n" +
                        "*1 ニベアUVシリーズ内における、UVカット効果(SPF50+/PA++++)および撥水効果において *2 日やけによるシミ・そばかすを防ぐ\n" +
                        "\n" +
                        "PROTECT&Care　紫外線から肌をまもり、美容ケアする2つのアプローチ\n" +
                        "\n" +
                        "強力紫外線をしっかりブロック\n" +
                        "\n" +
                        "●ハリ・弾力低下の原因となるUV-Aも、主にシミの原因となるUV-Bもしっかり防ぎます。\n" +
                        "●汗・水に強い3次元UVフィルムを採用。スーパーウォータープルーフ*で汗・水に触れても強力紫外線をカットします。(耐水試験で確認済み)*ブランド内において\n" +
                        "●海・プール・スポーツ・レジャー・強い日差しの外出時にもおすすめです。\n" +
                        "\n" +
                        "予防美容(日やけによるシミ予防)ができる美容ケアUV\n" +
                        "\n" +
                        "金銀花エキス*3や真珠タンパク抽出液*4など、リッチな美容液成分(保湿)を配合。\n" +
                        "日焼け止めということを忘れるくらい、なめらかで、まるで美容液のような肌感触です。肌に吸い付くようになじんで、驚くほどのうるおいと密着感を感じます。\n" +
                        "*3 スイカズラ花エキス　*4 加水分解コンキオリン\n" +
                        "\n" +
                        "どんなシーンでも好印象な清潔感あふれる香り\n" +
                        "\n" +
                        "夏にふさわしいさわやかなクリアフローラルの香り。せっけんやボディクリームを思わせる清潔感あふれる香りで、老若男女に好印象を与えます。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40085",
                    "40378",
                    "40010",
                    "40117",
                    "40163",
                    "40287",
                    "40302",
                    "40212",
                    "40091"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/3.jpg"
            ),
            mapOf<String, Any>(
                "name" to "UVイデア XL プロテクショントーンアップ ローズ",
                "brandId" to "30070",
                "categoryId" to "20007",
                "description" to "\"素肌のような透明感*1を演出。ピンクのトーンアップ*1UV、誕生。\n" +
                        "\n" +
                        "発売以来あらゆる媒体で28冠のベストコスメを受賞した大人気の保湿トーンアップ下地に、ついに待望の新色\"\"ローズ\"\"が登場!トーンアップ(色なし)と同様、SPF50+・PA++++の紫外線防御力でロングUVAはもちろん、PM2.5を含む大気中微粒子*2などの外的要因からも肌を守る独自のマルチプロテクションテクノロジーを搭載。さらにエイジングケア*3を考えたスキンケア成分や、肌をやわらげ、肌本来のバリア機能(角層)をサポートする、ラ ロッシュ ポゼ ターマルウォーター(水:整肌成分)配合。石鹸で落とせます。\n" +
                        "透明感と血色感をプラスしたいなら、新色・ピンクのトーンアップUVがおすすめです!\n" +
                        "\n" +
                        "塗った瞬間から実感できるトーンアップ*1効果\n" +
                        "光を乱反射し、肌を綺麗に魅せる トーンアップ*1テクノロジーを採用。くすみや色むらなど、気になる肌悩みを自然にカバー*1し、光のヴェールを纏っているような自然で上品なツヤ感を演出します。\n" +
                        "※メイクアップ効果によるもの\n" +
                        "\n" +
                        "SPF50+・PA++++の紫外線防御力\n" +
                        "シミなどの主な原因といわれるロングUVAもブロック。ラ ロッシュ ポゼ独自のUVフィルターシステムであるメギゾリルSX®、メギゾリルXL(ドロメトリゾールトリシロキサン)(紫外線防止剤)を採用。肌深部にまでダメージを与え、シミなどの原因を作り出すと言われるロングUVA(長波長UVA)領域もしっかり防御します。\n" +
                        "\n" +
                        "エイジングケア*3を考えたスキンケア成分を新配合\n" +
                        "気になる肌のエイジングケア*3を考えたスキンケア成分を新配合。肌をすこやかに保ち、うるおった、なめらかで輝くような肌へ導きます。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40378",
                    "40434",
                    "40191",
                    "40135",
                    "40267",
                    "40239",
                    "40036",
                    "40468",
                    "40042"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "パーフェクトUV スキンケアミルク",
                "brandId" to "30019",
                "categoryId" to "20007",
                "description" to "\"過酷な環境でもシミを防ぐ*1。最強*2UVミルク\n" +
                        "\n" +
                        "圧倒的なUV防御効果によって、過酷な紫外線環境から肌を守る。\n" +
                        "日常使いはもちろん、スポーツやレジャーシーンなどにおすすめの日焼け止めミルクです。\n" +
                        "\n" +
                        "UVブロック膜が均一に!オートブースター技術搭載\n" +
                        "汗・水・熱・空気中の水分*3に自動的に反応し、UVブロック膜が均一になって強くなるオートブースター技術を搭載。さらに、UV耐水性★★(スーパーウォータープルーフ)・耐こすれで紫外線からしっかりと肌を守ります。\n" +
                        "\n" +
                        "さらさらな使い心地で、石けんでするりと落ちるから使いやすい!\n" +
                        "驚くほどさらさらなテクスチャーで肌なじみが良く、ベタつかない快適な使用感。しっかりと紫外線をガードするのに石けんでするりと落ちて、日常使いにも便利なアイテムです。化粧下地としてもご利用いただけます。\n" +
                        "\n" +
                        "紫外線による乾燥ダメージを防ぐスキンケア成分配合\n" +
                        "スキンケア成分50%配合。植物由来成分をブレンドした保湿&美肌成分*4配合で、紫外線による乾燥ダメージなどを防ぎながら肌にうるおいを与えます。さらに肌に均一になじみ、強い太陽光でもふわっとした光で反射。透明感のある美肌印象に仕上げます。\n" +
                        "\n" +
                        "*1 日焼けによるシミを防ぐこと\n" +
                        "*2 最強とは、SPF50+、PA++++、及びアネッサ内のUV耐水性を意味する\n" +
                        "*3 暑い時期の高湿度環境\n" +
                        "*4 黄花エキス、緑茶エキス、異性化糖、グリセリン\n" +
                        "\n" +
                        "【こんなときにおすすめ】\n" +
                        "・スポーツやレジャーなど、強い日差しのもとで過ごすとき\n" +
                        "・海やプールなど、日焼け止めが落ちてしまいそうなとき\"",
                "ingredientIDs" to listOf(
                    "40183",
                    "40444",
                    "40435",
                    "40085",
                    "40226",
                    "40072",
                    "40212",
                    "40097",
                    "40157",
                    "40413"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "クロノビューティ ジェルUV EX",
                "brandId" to "30021",
                "categoryId" to "20007",
                "description" to "\"うるおってベタつかずキレイがつづく　汗・水こすれに強いUVジェル\n" +
                        "■ビーチフレンドリー処方:一部の国・地域・ビーチの規制に配慮した設計\n" +
                        "■プラスチック削減パッケージ(当社従来品比)\n" +
                        "■SPF50+ PA++++\n" +
                        "■スーパーウォータープルーフ※\n" +
                        "※ブランド内において\n" +
                        "■フリクションプルーフ\n" +
                        "■洗顔料・ボディソープで落とせる\n" +
                        "■チリ・ほこり・PM2.5・花粉等の微粒子汚れの付着を防ぐ※\n" +
                        "※すべての微粒子汚れの付着を防ぐわけではありません\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40435",
                    "40085",
                    "40212",
                    "40461",
                    "40091",
                    "40135",
                    "40413",
                    "40183",
                    "40267"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リップモンスター",
                "brandId" to "30038",
                "categoryId" to "20008",
                "description" to "\"落ちにくさも美発色もよくばりたい、落ちにくい口紅。\n" +
                        "唇から蒸発する水分を活用して密着ジェル膜に変化する独自技術※で、つけたての色が長時間持続。\n" +
                        "高発色&保湿。\n" +
                        "\n" +
                        "※当社独自の色持ち技術\"",
                "ingredientIDs" to listOf(
                    "40236",
                    "40096",
                    "40073",
                    "40451",
                    "40398",
                    "40284",
                    "40432",
                    "40224",
                    "40354",
                    "40453"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ネンマクフェイク ルージュ",
                "brandId" to "30025",
                "categoryId" to "20008",
                "description" to "軽いつけ心地とツヤ質感で唇に均一にフィット。つけたての色が落ちにくい粘膜色リップ。",
                "ingredientIDs" to listOf(
                    "40179",
                    "40231",
                    "40072",
                    "40261",
                    "40252",
                    "40236",
                    "40432",
                    "40183",
                    "40313",
                    "40071"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "エッセンス リッププランパー ",
                "brandId" to "30025",
                "categoryId" to "20008",
                "description" to "じんじんとした刺激感&心地よい清涼感でぷっくりボリュームのある唇を叶え、乾燥を防ぐリッププランパー。",
                "ingredientIDs" to listOf(
                    "40263",
                    "40406",
                    "40451",
                    "40351",
                    "40234",
                    "40225",
                    "40184",
                    "40290",
                    "40244",
                    "40298"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リップカラーシールド",
                "brandId" to "30046",
                "categoryId" to "20008",
                "description" to "\"色艶を抱えたオイルが、唇の水分と反応しゲル化してピタッと密着。塗りたての色を守る、ジェル膜処方のリップ。\n" +
                        "5種の美容保湿成分(ウラボシヤハズエキス、アルガニアスピノサ核油、ラウロイルグルタミン酸ジ(フィトステリル/オクチルドデシル)、シア脂油、ホホバ種子油)配合。\n" +
                        "柔らかい赤みを感じる、万能ピンクブラウン。\"",
                "ingredientIDs" to listOf(
                    "40179",
                    "40253",
                    "40248",
                    "40024",
                    "40180",
                    "40406",
                    "40338",
                    "40330",
                    "40252",
                    "40432"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "フジコ ニュアンスラップティント ",
                "brandId" to "30062",
                "categoryId" to "20008",
                "description" to "\"水17%!フジコ初のウォーターティント処方ルージュは、ツヤも発色もマスクにつきにくい「落ちない」が前提のツヤ仕上げ♪\n" +
                        "オイルだけでなくとろみ成分配合だから潤ってもっちり!縦ジワをカバーします。\n" +
                        "肌なじみのいいニュアンスカラーが唇に溶け込みカジュアルな色気を演出。\"",
                "ingredientIDs" to listOf(
                    "40303",
                    "40444",
                    "40179",
                    "40096",
                    "40406",
                    "40135",
                    "40016",
                    "40087",
                    "40070",
                    "40331"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "チークブラッシュ",
                "brandId" to "30046",
                "categoryId" to "20009",
                "description" to "\"密着性が高く頬に馴染み、まるで肌の内側からにじみ出るような自然な血色感を演出します。アイシャドウとしてもお使いいただけます。\n" +
                        "5種の美容保湿成分(カニナバラ果実油、ホホバ種子油、オリーブ果実油、マカデミア種子油、スクワラン)配合。\n" +
                        "肌あたりの良い毛量たっぷりのブラシ付。自然な大人の血色感を演出するローズピンクカラー。\"",
                "ingredientIDs" to listOf(
                    "40463",
                    "40252",
                    "40406",
                    "40352",
                    "40366",
                    "40331",
                    "40315",
                    "40373",
                    "40244",
                    "40191"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "パールグロウハイライト",
                "brandId" to "30046",
                "categoryId" to "20009",
                "description" to "\"くすみやクマを光で飛ばして明るさを出し、お顔を立体的に見せます。\n" +
                        "美容液成分(ヒアルロン酸Na・ラベンダー花エキス・カミツレ花エキス・ローズマリー葉エキス)配合。ブラシ付。\"",
                "ingredientIDs" to listOf(
                    "40252",
                    "40253",
                    "40004",
                    "40323",
                    "40366",
                    "40001",
                    "40331",
                    "40303",
                    "40315",
                    "40275"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "フェイスグロウカラー ",
                "brandId" to "30046",
                "categoryId" to "20009",
                "description" to "\"ハイライト・チーク・アイシャドウなどマルチに使える生ぷにっ質感のフェイスカラー。\n" +
                        "塗った瞬間サラッとしてヨレにくく、オイルをまとったようなうるみ艶をプラスします。水・汗・皮脂に強く落ちにくい。\n" +
                        "美容保湿成分10種*1配合。\n" +
                        "馴染みのいいデイリーベージュのハイライトと、ベージュを1滴足したアプリコットの血色カラーの組み合わせです。\n" +
                        "\n" +
                        "*1スクワラン、ホホバ種子油、マンゴー種子脂、ヒマワリ種子油、マカデミア種子油、ヤシ油、セラミドNP、ローズマリー葉エキス、アロエベラ葉エキス、加水分解コラーゲン\"",
                "ingredientIDs" to listOf(
                    "40253",
                    "40183",
                    "40431",
                    "40003",
                    "40406",
                    "40187",
                    "40463",
                    "40354",
                    "40284",
                    "40191"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "クリームチーク(パールタイプ)",
                "brandId" to "30034",
                "categoryId" to "20009",
                "description" to "\"”むにゅっ”とした生レアな質感で、伸びが良く、お肌に密着します。パール高配合で、うるっと艶のある仕上がり。\n" +
                        "水・汗・皮脂・こすれに強く、長時間ヨレにくいです。\"",
                "ingredientIDs" to listOf(
                    "40253",
                    "40183",
                    "40187",
                    "40003",
                    "40431",
                    "40191",
                    "40104",
                    "40334",
                    "40115",
                    "40354"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "シームレストーン ブラッシュ",
                "brandId" to "30027",
                "categoryId" to "20009",
                "description" to "\"1.ほんのりと彩りを添えるヌードカラー。境目なく頬に馴染む肌溶けチーク。\n" +
                        "2.素肌を活かすソフトな発色。内側からにじみ出るような自然な立体感を演出。\n" +
                        "3.粉っぽさのない密着パウダー処方。透明感のあるピュアなカラーを長時間キープ。\n" +
                        "4.お肌のうるおいを守る成分※in。\n" +
                        "5.肌あたりやわらかなオリジナルブラシ付き。斜めカットのラウンド計上で、広く均一に塗りやすい。\n" +
                        "\n" +
                        "※セラミドNP、スクワラン、ホホバ種子油、オリーブ果実油(すべて保湿)\n" +
                        "\n" +
                        "■SB01(シーショア):透明感を仕込むピュアブルー。\"",
                "ingredientIDs" to listOf(
                    "40226",
                    "40253",
                    "40463",
                    "40346",
                    "40395",
                    "40260",
                    "40372",
                    "40198",
                    "40213",
                    "40191"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "タンイドル ウルトラ ウェア リキッド",
                "brandId" to "30071",
                "categoryId" to "20010",
                "description" to "\"長時間美しさ続くカバー力、呼吸するような心地良さ\n" +
                        "\n" +
                        "ランコムから8年ぶりに待望のファンデーションが2018年に誕生しました。発売後すぐにランコムで売上No.1*1になった通称\"\"ウルトラファンデ\"\"。\n" +
                        "\n" +
                        "高いカバー力で毛穴の見えない*2美肌が長時間続くのに、厚塗り感を感じない、まるで肌が呼吸するような感触軽くて心地良いファンデーションです。女性がファンデーションに求めるカバー力、化粧崩れのなさ、そして心地良さを同時に叶えたまさに夢のようなファンデーションです。\n" +
                        "\n" +
                        "毛穴の見えない*2美しい「カバー力」\n" +
                        "肌に塗布した瞬間に、ソフトフォーカスパウダーとピグメントが整列するように均一に並び、フォーミュラ内の揮発成分が蒸発するとともに肌に高密着します。高いカバー力の秘密は、この紛体の並びの“均一性\"\"にあります。肌の赤味や黄味、シミや色ムラも自然に補正してくれる高いカバー効果を発揮し、毛穴プルーフ美肌へと導きます。\n" +
                        "\n" +
                        "長時間美しさが続く 「ロングラスティング効果」\n" +
                        "搭載された「ウルトラネットテクノロジー」により、美しさが続きます。肌に伸ばすと、ポリマーがネット状の化粧膜を形成し、肌に密着。崩れを防止します。2種の皮脂吸着パウダーも化粧崩れを防ぎます。日本のような高い湿度の環境でも長時間ファンデーションが崩れることなく美しさが持続します。\n" +
                        "\n" +
                        "厚塗り感を感じない「心地良さ」を実現\n" +
                        "エアーウェアテクノロジー搭載で、まるでつけていないかのような軽さと肌が吸収するような心地良さが続きます。\n" +
                        "\n" +
                        "*1 集計期間:2018年5月～8月までのランコム内ファンデーション売上個数に基づく\n" +
                        "*2 メイクアップ効果\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40183",
                    "40072",
                    "40468",
                    "40378",
                    "40260",
                    "40010",
                    "40434",
                    "40023",
                    "40274"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/4.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ゼン ウェア フルイド",
                "brandId" to "30041",
                "categoryId" to "20010",
                "description" to "\"●和墨からインスパイアされた、墨テクノロジーを採用。ねめらかな感触で薄く均一に密着し、つけたての美しい仕上がりが持続します。\n" +
                        "●汗・皮脂・こすれ・高温多湿に強く、二次付着レスで、1日中化粧くずれを防ぎます。\n" +
                        "●オールシーズンお使いいただけます。\n" +
                        "●フリー処方(パラベンフリー/鉱物油フリー)\n" +
                        "●アレルギーテスト済み/ノンコメドジェニックテスト済み ※24時間とは当社調べ、効果には個人差があります。 ※すべてかたにアレルギーが起きない。コメド(ニキビのもと)ができないというわけではありません。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40172",
                    "40226",
                    "40183",
                    "40085",
                    "40257",
                    "40378",
                    "40073",
                    "40187",
                    "40049"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "コンフォートスキン ウェア",
                "brandId" to "30004",
                "categoryId" to "20010",
                "description" to "\"「素肌、晴れわたる*1」ファンデーション\n" +
                        "\n" +
                        "瞬時に明るく冴えわたり、心地よさまでも伝わるような肌へ。\n" +
                        "溶け込むようになじむ、やわらかでしなやかな塗膜が長時間持続し、ふんわりと明るい肌になりすます「スキンコンフォートテクノロジー」搭載。粉感や白浮き感なく肌と一体化してよれにくく、化粧崩れを抑え、表情までのびやかに晴れわたる*1心地よい仕上がりが続きます。\n" +
                        "\n" +
                        "思わず触りたくなるほど、心地よさまでも伝わるような肌へ\n" +
                        "自分の肌が美しくなりかわったかのように錯覚するカバー力と、思わず触りたくなるほどの心地よさを両立。\n" +
                        "\n" +
                        "「素肌、晴れわたる*1」嬉しさを叶える3つのカギ\n" +
                        "\n" +
                        "1.つけた瞬間、肌と一体化\n" +
                        "溶け込むような肌なじみで、シームレスにピタッと密着。心地よくのび広がる、やわらかなつけ心地。\n" +
                        "\n" +
                        "2.瞬時に明るくカバー\n" +
                        "ふんわり明るく冴えわたるツヤ肌仕上がり。粉感や白浮き感なく、美しい肌になりすますカバー力。\n" +
                        "\n" +
                        "3.心地よい仕上がりが1日*2持続\n" +
                        "よれにくく、崩れにくい。密着感がありながら、素肌っぽい質感とうるおいを1日*2キープ。表情までのびやかに晴れわたる*1仕上がり。\n" +
                        "\n" +
                        "\"\"心地よさ伝わる\"\"KANEBO新技術*3「スキンコンフォートテクノロジー」搭載\n" +
                        "崩れにくいのに、やわらかで心地よい。しなやかな塗膜が長時間持続し、ふんわりと明るい肌になりすますKANEBOの新技術*3。濃密なオイルと赤ちゃんの未熟な肌を包む「胎脂」に着想を得て開発したクリーム処方を構成する成分を組み合わせた複合保湿成分で、体温でとろけるように肌になじむ「モイストバウンシングオイルコンパウンド」が、肌のキメなどの凹凸に均一に密着してよれにくい「フィックスパウダーゲル」を包み込み、肌への密着性高く、粉感レスで鮮やかな発色の「クリアカラーファインピグメント」を均一に分散させます。感触・化粧持ち・仕上がりの3つの妙で実現。\n" +
                        "\n" +
                        "塗っている方が心地よい。自分らしい。そんな「新しい素肌」へ\n" +
                        "やわらかくなめらかなテクスチャーで、表情までのびやかに晴れわたるような心地よさを感じる仕上がりへ。重ねづけしても厚塗り感なく、自然なカバー力を発揮。化粧下地、コンシーラー、フェースパウダー要らずがうれしい1本。肌のうるおいを1日*2キープし、触れたくなるようなふんわり明るいツヤ肌へ仕上げる美容液ファンデーションです。\n" +
                        "\n" +
                        "*1 メイク効果により素肌が明るく仕上がること\n" +
                        "*2 朝塗布してから夕方落とすまで\n" +
                        "*3 カネボウ化粧品として\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40372",
                    "40183",
                    "40088",
                    "40378",
                    "40016",
                    "40010",
                    "40180",
                    "40135",
                    "40208"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ダブル ウェア ステイ イン プレイス メークアップ",
                "brandId" to "30028",
                "categoryId" to "20010",
                "description" to "\"商品の説明\n" +
                        "エスティ ローダーのベストセラー&ロングセラー ファンデーション\n" +
                        "1998年の発売以来、ベストセラー&ロングセラー製品として絶大な支持を得ている、エスティローダーのNo.1*1ファンデーション。\n" +
                        "狙い通りの素肌感、想像以上のカバー力。くずれにくいストレス*2フリー肌へ。マスク生活のキレイを応援します!\n" +
                        "\n" +
                        "【ダブルウェアの4つのポイント】\n" +
                        "・狙い通りの素肌感、一日中続く\n" +
                        "・至近距離でも毛穴ゼロ。想定以上のカバー力\n" +
                        "・マスクにもくずれにくい、ストレス*2フリー肌へ。\n" +
                        "・日本人の肌に合わせた豊富な色展開\n" +
                        "\n" +
                        "*1 2020年1月～12月の当社製品の売上比較による\n" +
                        "*2 くずれによる不快感\n" +
                        "\n" +
                        "【夕方までメークがくずれない?ダブル ウェアのテクスチャーがもつ秘密は?】\n" +
                        "ダブル ウェアは、「シリコーン中水」処方を採用。\n" +
                        "そのため、肌に塗布した後持続性ポリマーが、ピタッと肌に密着。表情の動きに合わせてしなやかに伸びる柔軟なネットワーク構造を形成するから、時間が経ってもくずれない。\n" +
                        "\n" +
                        "※ ボトルの表記に関わらず、紫外線防御指数は、SPF 10/PA++です。正しい表記は、ボトル裏面のラベルを参照ください。\n" +
                        "至近距離でも隙のない仕上がり。ダブル ウェアで、いつでも自信をまとう肌へ。\n" +
                        "\n" +
                        "1998年の発売以来、ベストセラー&ロングセラー製品として絶大な支持を得ている、エスティローダーのNo.1*¹ファンデーション。至近距離でも毛穴が気にならないほど、高いカバー力。朝の仕上がり、一日中続くキープ力。隙のない仕上がりで、輝き続ける自信美肌へ。\n" +
                        "\n" +
                        "*1 2019年1月～12月の当社製品の売上比較による\n" +
                        "\n" +
                        "1998年から続くロングセラーで、自信をまとう「ダブル ウェア」嬉しい4つのメリット。\n" +
                        "\n" +
                        "• 至近距離でも毛穴ゼロのカバー力\n" +
                        "• 朝の仕上がり、一日中続くキープ力\n" +
                        "• 日本人の肌に合わせた豊富な色展開\n" +
                        "• 汗や皮脂に強く、肌にピタッと密着して崩れない\n" +
                        "夕方までメークが崩れない?ダブル ウェアのテクスチャーがもつ秘密は?\n" +
                        "\n" +
                        "ダブル ウェアは、「シリコーン中水」処方を採用しています。\n" +
                        "そのため、肌に塗布した後持続性ポリマーが、ピタッと肌に密着。表情の動きに合わせてしなやかに伸びる柔軟なネットワーク構造を形成するから、時間が経っても崩れないのです。\n" +
                        "\n" +
                        "理想のカラーが見つかる、日本人の肌に合わせた豊富な色展開\n" +
                        "\n" +
                        "カバー力に優れるからこそ、ぴったりの色を選びたいファンデーション。ダブル ウェアは、ピンク・オークル・ベージュ、3つの色相から最大4段階の明るさが選べるので、理想のカラーがきっと見つかります。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40171",
                    "40172",
                    "40260",
                    "40010",
                    "40160",
                    "40472",
                    "40259",
                    "40352",
                    "40396"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ザ ファンデーション リフトグロウ",
                "brandId" to "30017",
                "categoryId" to "20010",
                "description" to "\"商品の説明\n" +
                        "まるでハイライトのように、高い部分に光を集めてリフレクトさせ、リフトアップしたような洗練のメリハリを作るファンデーション。\n" +
                        "肌と一体化する薄膜でコンフォートにカバーし、丁寧にスキンケアをしたようなライブリーな肌印象に仕上げます。\n" +
                        "\n" +
                        "[製品特長]\n" +
                        "■ ストレッチリフトテクノロジーとトリプルグロウオイル採用。光を操り、ピンとした立体的なツヤとハリ感を実現\n" +
                        "■ マイクロレベルの微細粉体採用で、肌に溶け込むような自然さ。重ねても厚塗り感がなく、カバーコントロール自在\n" +
                        "■ オールデイラスティング\n" +
                        "■ 美容液83%*配合で、日中の肌を保湿\n" +
                        "■ 大気中のちりやほこりなどの微粒子から肌を守ります　\n" +
                        "■ SPF20・PA++\n" +
                        "■ ノンコメドジェニックテスト済み**\n" +
                        "■ 鉱物油・パラベン・グルテンフリー、ヴィーガンフレンドリー\n" +
                        "■ 無香料\n" +
                        "\n" +
                        "* 美容液とは、粉体を除くエマルジョンのことです\n" +
                        "** すべてのかたにコメド(ニキビのもと)ができないというわけではありません\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40171",
                    "40378",
                    "40226",
                    "40085",
                    "40406",
                    "40073",
                    "40180",
                    "40377",
                    "40252"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ヴォワールコレクチュールn",
                "brandId" to "30037",
                "categoryId" to "20011",
                "description" to "\"理想の、それ以上の、肌がつづく鍵。\n" +
                        "\n" +
                        "スキンケアのような心地よさで、すばやく肌のトーンを整える*1マルチパフォーマンスプライマー。進化したテクノロジーで、肌悩みをカバーするだけでなく、肌そのものを美しくケアします。今も未来も、完璧を夢みる肌へ。\n" +
                        "\n" +
                        "肌表面の乱れとくすみを瞬時に補正、美しい素肌のように整える\n" +
                        "肌の凹凸・小じわ・くすみなどを瞬時に目立たなくし、明るくトーンアップした印象に。まるで素肌そのものが美しくなったかのように、キメの整ったなめらかな質感を演出しながら、ファンデーションの仕上がりを高めます。\n" +
                        "\n" +
                        "輝きとうるおいを感じさせる、美しい仕上がりが長時間持続\n" +
                        "メイクアップとスキンケアを融合した独自技術「ライトエンパワリングエンハンサー」が、光を操って輝きあふれる仕上がりへ。皮脂によるテカリ・ヨレ・色くすみなどを防ぎ、メイクしたての美しい肌を長時間持続します。また、“肌の知性*2\"\"に着目した共通独自成分「スキンイルミネイター*3(保湿・整肌)」を配合。\n" +
                        "\n" +
                        "肌に喜びをもたらす、贅沢な使用感\n" +
                        "上質なスキンケアクリームのようになめらかな使い心地で、肌へ軽やかに溶け込みます。ノンコメドジェニックテスト・アレルギーテスト済み*4。天然ローズエッセンスなどを調香した上品な香りです。\n" +
                        "\n" +
                        "*1 メイクアップ効果による\n" +
                        "*2 「肌の知性」とは、すべての人が生まれながらにそなえている、生涯美しい輝きを保ち続けるための鍵です\n" +
                        "*3 加水分解シルク、加水分解コンキオリン、テアニン、トウキエキス、シソエキス、グリシン、グリセリン、PEG/PPG-14/7ジメチルエーテル、トレハロース\n" +
                        "*4 全ての方にニキビができない、アレルギーが起きないというわけではありません\n" +
                        "\n" +
                        "【こんな方におすすめ】\n" +
                        "\n" +
                        "・うるおい感ある使用感が好みの方\n" +
                        "・トーンアップした印象のなめらかな肌に整えたい方\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40183",
                    "40010",
                    "40076",
                    "40135",
                    "40434",
                    "40468",
                    "40352",
                    "40378",
                    "40269"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ドラマティックスキンセンサーベース NEO ",
                "brandId" to "30064",
                "categoryId" to "20011",
                "description" to "\"商品の説明\n" +
                        "テカリ・カサつきをダブルで防ぎ、スキンケアまで叶う毛穴レスくずれ防止下地。\n" +
                        "〇テカり・カサつき防止　皮脂・水分量をコントロールして、1日中*1くずれないうるさらセンサーコート配合。\n" +
                        "〇毛穴補正効果　毛穴補正パウダーをコートするジェリーがとろけるように凹凸をうめながら、毛穴をぼかしてカバー。\n" +
                        "〇浸透型うるおい美容液　角層のすみずみまでうるおいが届き、持続。13時間化粧もち*2データ取得済み。\n" +
                        "\n" +
                        "*1(マキアージュ調べ。効果には個人差があります。)\n" +
                        "*2(テカり・皮脂くずれ・毛穴の目立ち・よれ・薄れ・くすみ・粉っぽさ)のなさ(マキアージュ調べ。効果には個人差があります。)\"",
                "ingredientIDs" to listOf(
                    "40183",
                    "40444",
                    "40212",
                    "40435",
                    "40378",
                    "40187",
                    "40085",
                    "40117",
                    "40434",
                    "40010"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "トーンパーフェクティング パレット",
                "brandId" to "30041",
                "categoryId" to "20011",
                "description" to "\"お肌と一体化するようになじんで、\n" +
                        "肌色を均一にととのえ、\n" +
                        "素肌感を美しく引き立てるコンシーラーパレット。\n" +
                        "\n" +
                        "●シミ・くま・ニキビ跡・色ムラ・凹凸をなめらかにしながら、瞬時にカバー。つけたての美しい質感がつづきます。\"",
                "ingredientIDs" to listOf(
                    "40252",
                    "40183",
                    "40368",
                    "40164",
                    "40168",
                    "40411",
                    "40253",
                    "40187",
                    "40432",
                    "40284"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "CPチップコンシーラー",
                "brandId" to "30013",
                "categoryId" to "20011",
                "description" to "\"速乾性が高いので、お肌の悩みを即カバー(メイクアップ効果)。\n" +
                        "植物由来の保湿成分配合で、内側はしっとり、表面はさらっと自然なうるおいが長時間持続。\n" +
                        "ファンデーションを使わなくても、「ザ・セム」のチップコンシーラーとお粉があれば、理想のベースメイクがスピーディーに完成します。\n" +
                        "もちろん、リキッドファンデやパウダーファンデと併用してもOK。\n" +
                        "SPF28・PA++で、気になる紫外線もしっかりガードします。\n" +
                        "\n" +
                        "「1.5　ナチュラルベージュ」は、赤みが気になるニキビ跡や肌荒れ、くすみやクマなどの肌悩みにこれ1本。\n" +
                        "一般的な肌色の方に対応するオールマイティなカラーです。\"",
                "ingredientIDs" to listOf(
                    "40434",
                    "40444",
                    "40171",
                    "40313",
                    "40226",
                    "40010",
                    "40208",
                    "40041",
                    "40023",
                    "40415"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "カネボウ デザイニングカラーリクイド",
                "brandId" to "30004",
                "categoryId" to "20011",
                "description" to "\"カバーしながら、肌になりきる。つけたての美しさとうるおいが続く、美容液コンシーラー\n" +
                        "\n" +
                        "カバーしながら肌になりきる、美容液コンシーラー。気になる肌悩み、パーツごとに色と質感を追及。\n" +
                        "テクニックいらずで自然に肌悩みをカバーします。\n" +
                        "肌なじみがよく、動きの多い目もと・口もとにもよれずにピタっと密着し、またうるおいが長時間持続します。\n" +
                        "広範囲にも細かい部分にも塗りやすいやわらかなスパチュラ状チップ採用。\n" +
                        "ファンデーションとしても使えます。また、化粧直しのポイント使いも簡単にできるので携帯に便利です。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40434",
                    "40010",
                    "40135",
                    "40016",
                    "40358",
                    "40372",
                    "40180",
                    "40452",
                    "40167"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ライトリフレクティングセッティングパウダー プレスト N",
                "brandId" to "30006",
                "categoryId" to "20012",
                "description" to "\"人気を誇るコンプレクションアイテム「ライトリフレクティングセッティングパウダー プレスト N」の内容量が増加し、パッケージを刷新。\n" +
                        "\n" +
                        "ファンデーションの仕上がりを良くし、メーキャップを長持ちさせる無色のパウダー。\n" +
                        "フォトクロミックテクノロジーを採用したシルクのようなパウダーは、光を拡散させ、一日中変化するさまざまな光に適応します。\n" +
                        "光を操ることで、小じわや毛穴の目立たないなめらかな肌に仕上げます。\"",
                "ingredientIDs" to listOf(
                    "40431",
                    "40187",
                    "40089",
                    "40135",
                    "40191",
                    "40200",
                    "40350",
                    "40116",
                    "40090",
                    "40245"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ソフトマット アドバンスト パーフェクティングパウダー",
                "brandId" to "30006",
                "categoryId" to "20012",
                "description" to "\"メイクをロック。テカリをブロック。肌を瞬時にぼかしてなめらかにし、乾燥や粉っぽさも感じさせないパーフェクトスキンを作るマルチユースなパウダーの登場。\n" +
                        "\n" +
                        "マルチユースなパウダーが新登場。\n" +
                        "革新的なタルクフリーフォーミュラは毛穴を目立たなくさせ、テカリを抑えます。肌の上でさらっとすべるようになじみ、シルクのように仕上げるフォーミュラで過剰な皮脂を抑えてマットな仕上がりが持続。\n" +
                        "単独で使えばソフトマットなカバーでナチュラルな仕上がりに、ソフトマットコンプリート ファンデーションと重ねればメイクもちが長時間持続。\n" +
                        "\n" +
                        "03122 ニュートラルトーンの非常に明るいシェード\"",
                "ingredientIDs" to listOf(
                    "40431",
                    "40333",
                    "40353",
                    "40187",
                    "40366",
                    "40183",
                    "40180",
                    "40463",
                    "40112",
                    "40206"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ル・レオスールデクラ",
                "brandId" to "30037",
                "categoryId" to "20012",
                "description" to "\"プレシャスオパールの輝きに着目して生まれた、表情を美しく際立たせるハイライティングパウダーです。\n" +
                        "動いているときも、静止した瞬間も、内側から光を放つように上品なつややかさで、３６０度から目を惹きつける、輝く印象へと導きます。\n" +
                        "ライトエンパワリングトリートメントパウダー（肌あれ防止）配合。（硫酸Ba）\"",
                "ingredientIDs" to listOf(
                    "40226",
                    "40438",
                    "40187",
                    "40411",
                    "40253",
                    "40064",
                    "40183",
                    "40435",
                    "40061",
                    "40102"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "マシュマロフィニッシュパウダー",
                "brandId" to "30034",
                "categoryId" to "20012",
                "description" to "\"まるで柔らかいマシュマロみたいな白美肌で、女の子らしい甘顔が完成するフェイスパウダー。\n" +
                        "ベタつきもテカリもサラリとかわして、思わず触りたくなるようなふんわり美肌に!\n" +
                        "毛穴や色ムラをキレイにカバーしてくれるのに、厚塗り感なしでナチュラルな仕上がり。\n" +
                        "美容液成分配合で、お肌に優しくオールシーズンお使い頂けます。\n" +
                        "\n" +
                        "☆マットオークル\n" +
                        "☆ふんわりパフ付き\n" +
                        "☆無香料・アルコールフリー\n" +
                        "\n" +
                        "※パッケージ切り替えにより、画像とは異なるパッケージの製品が届く可能性がございます。\"",
                "ingredientIDs" to listOf(
                    "40353",
                    "40378",
                    "40187",
                    "40183",
                    "40253",
                    "40252",
                    "40435",
                    "40445",
                    "40193",
                    "40373"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "フィニッシングパウダー マット",
                "brandId" to "30049",
                "categoryId" to "20012",
                "description" to "\"フィニッシング パウダー 761 ナチュラル　のリニューアル商品です\n" +
                        "※成分変更あり\n" +
                        "\n" +
                        "発色を損なわずにファンデーションを安定させ、くずれにくい肌へ。\n" +
                        "肌のキメや毛穴を整え、テカリを抑えてさらさら感のある仕上がりが続きます。(※1)\n" +
                        "オーガニック認証のうるおい成分(※2)を配合し、国産のリポアミノ酸処理セリサイトを使用しています(※3)。\n" +
                        "※パフは別売ですので、ご注意ください。\"",
                "ingredientIDs" to listOf(
                    "40353",
                    "40296",
                    "40293",
                    "40292",
                    "40288",
                    "40183",
                    "40061",
                    "40159",
                    "40240",
                    "40104"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/5.jpg"
            ),
            mapOf<String, Any>(
                "name" to "エッセンスインヘアミルク",
                "brandId" to "30033",
                "categoryId" to "20014",
                "description" to "\"パサつき、乾燥による広がり、ツヤ不足…そんな髪のお悩みを解決する、洗い流さないタイプのトリートメント。\n" +
                        "髪の芯まで浸透する美容液成分*1とうるおいをキープするミルクの2つの良さを1つに凝縮。\n" +
                        "ドライヤーの熱を味方に、擬似キューティクルを作り出すことで、サラサラつるんの指通りを実現します。\n" +
                        "高保水ミルク*2が、うるおいを逃がさないように髪表面をコート。\n" +
                        "乾燥などのダメージを受けがちな髪へ、内外からのしっかりケアで、うるおい健やかな美髪をずっとキープします。\n" +
                        "\n" +
                        "『エッセンスインヘアミルク』が人気の理由\n" +
                        "\n" +
                        "◎ベタつかない美容液入りミルク\n" +
                        "なじませた瞬間、美容液がとろけ出し、みずみずしい感触に変化します。\n" +
                        "\n" +
                        "◎美容液inミルク処方\n" +
                        "髪の芯まで浸透する美容液成分*1とうるおいをキープするミルク。\n" +
                        "2つの良さを1つに凝縮しました。ベタつかず、しなやかなツヤ髪を実現します。\n" +
                        "\n" +
                        "◎無香料\n" +
                        "シャンプーや香水などの他の香りと混ざらないのも人気なポイントです。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40183",
                    "40207",
                    "40164",
                    "40077",
                    "40222",
                    "40271",
                    "40024",
                    "40358",
                    "40062"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ReFa LOCK OIL",
                "brandId" to "30072",
                "categoryId" to "20014",
                "description" to "\"熱を味方に、しなやかにロックするツヤめくスタイルがつづく\n" +
                        "\n" +
                        "カールもストレートも、崩さずに保ちたい。\n" +
                        "しなやかな指通りもツヤも、譲れない。\n" +
                        "そんなスタイリストのお悩みに、アイロン前のワンステップ。\n" +
                        "\n" +
                        "プロフェッショナルの熱コントロールに着想を得た独自の処方で、カールもストレートも、時間がたってもロックをかけたまま。\n" +
                        "髪表面を固めないので、驚くほどしなやかな指どおりとツヤを叶えます。\n" +
                        "\n" +
                        "美しいヘアスタイルにロックをかけて、1日中、心を躍らせて。\n" +
                        "\n" +
                        "================\n" +
                        "植物由来の香料で香りを付けています。\n" +
                        "一般的な基礎化粧品に含有されてる香料を使用しておりますのでご安心ください。\n" +
                        "※但し、お肌の状態等は、個人差がございますので、異常を感じられた場合は、\n" +
                        "一度使用をおやめいただき、ご様子みてください。\n" +
                        "================\"",
                "ingredientIDs" to listOf(
                    "40277",
                    "40461",
                    "40236",
                    "40246",
                    "40223",
                    "40057",
                    "40061",
                    "40134",
                    "40319",
                    "40357"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ザビューティ 髪のキメ美容ウォータートリートメント",
                "brandId" to "30030",
                "categoryId" to "20014",
                "description" to "\"洗い流さないタイプのウォータートリートメント。美容ウォーターがぐんぐん浸透。持続型ウォーターヴェールで、感動的なつるサラ髪※、1日続く。\n" +
                        "\n" +
                        "〇ダメージ補修&予防 *(*アイロン・ブラシの摩擦によるパサつき)\n" +
                        "〇ドライヤー速乾&乾かしすぎによるパサつきを防ぐ\n" +
                        "○美しい髪の必須成分18-MEA(ラノリン脂肪酸:毛髪保護)配合\n" +
                        "〇保湿成分配合(加水分解コラーゲン、乳酸)\n" +
                        "○フローラルリュクスの香り\n" +
                        "\n" +
                        "※ブランド調べ。効果には個人差があります\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40085",
                    "40016",
                    "40420",
                    "40401",
                    "40465",
                    "40348",
                    "40349",
                    "40344",
                    "40315"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "AQ ブースティング トリートメント ヘアセラム ",
                "brandId" to "30041",
                "categoryId" to "20014",
                "description" to "\"髪へ「導入美容液」という、答え。皮膚科学研究の英知が生んだ新・ヘアセラムで、髪の芯まで浸透・補修。\n" +
                        "\n" +
                        "髪の芯まで浸透・補修し、毛先までなめらかにまとまる。\n" +
                        "髪の導入美容液として、トリートメントの前に使用するウォーターベースのヘアセラム。\n" +
                        "\n" +
                        "贅沢なうるおいが傷んだ毛髪の内部に浸透し、\n" +
                        "乾燥・カラーリング・摩擦などによるダメージを補修する、\n" +
                        "スペシャルケア用トリートメント ヘアセラムです。\n" +
                        "毛先までしっとりまとまる、上質でなめらか髪に仕上げます。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40170",
                    "40085",
                    "40010",
                    "40036",
                    "40258",
                    "40199",
                    "40114",
                    "40146"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "トリートメントヘアウォーター",
                "brandId" to "30033",
                "categoryId" to "20014",
                "description" to "\"ひと吹きでまとまるサラツヤ髪へ。\n" +
                        "忙しい朝も夜の集中ケアにも使えるトリートメントミスト\n" +
                        "\n" +
                        "■おすすめポイント\n" +
                        "・シュッとひと吹きで髪がまとまり、驚くほどなめらかな指通りに\n" +
                        "・髪の内側のダメージもしっかり補修するから、仕上がりは驚くほどふわっとなめらか\n" +
                        "・夜のドライヤー前に使えば、サロン帰りのなめらかさと指通りに。朝の寝ぐせ直しにもおすすめ\n" +
                        "・スプレーしやすいトリガータイプのボトル\n" +
                        "\n" +
                        "■こんな方におすすめ\n" +
                        "・指通りが悪く、髪にツヤも不足している\n" +
                        "・毎朝、寝ぐせがつきやすい\n" +
                        "・髪が根元から傷んでいる気がする\n" +
                        "\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40085",
                    "40058",
                    "40135",
                    "40209",
                    "40222",
                    "40142",
                    "40422",
                    "40322",
                    "40474"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ハトムギ化粧水",
                "brandId" to "30053",
                "categoryId" to "20015",
                "description" to "\"●19種のアミノ酸を含む天然保湿成分「ハトムギエキス」配合の化粧水です。\n" +
                        "●みずみずしく浸透力に優れた処方なので、重ねるほどぐんぐん肌になじみ、角質層を水分で満たします。\n" +
                        "●たっぷり重ねづけをしてもべたつかない、さっぱりした使用感です。\n" +
                        "●無香料、無着色、低刺激性、アルコールフリー、オイルフリー、界面活性剤フリー。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40016",
                    "40010",
                    "40135",
                    "40282",
                    "40137",
                    "40002",
                    "40131",
                    "40132",
                    "40373"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ディープモイスチャースプレー",
                "brandId" to "30035",
                "categoryId" to "20015",
                "description" to "\"肌荒れ・カサつきをくり返しがちな 乾燥性敏感肌に\n" +
                        "\n" +
                        "微細化したセラミド機能成分*が肌の奥(角層)まで浸透。ひと吹きで潤ったやわらかな肌に保ちます。\n" +
                        "*ヘキサデシロキシPGヒドロキシエチルヘキサデカナミド\n" +
                        "\n" +
                        "●消炎剤配合(有効成分)配合。肌荒れを防ぐ。\n" +
                        "●潤い成分(ユーカリエキス)配合。\n" +
                        "●ケアしづらい背中や腰など、全身に。\n" +
                        "●一日中、顔やからだにいつでもどこでも潤い補給。お風呂あがりに。メイクの上から。お子さまのデリケートな肌にも。(医薬部外品)\n" +
                        "\n" +
                        "乾燥性敏感肌を考えた低刺激設計\n" +
                        "○弱酸性　○無香料　○無着色　○アルコールフリー(エチルアルコール無添加)\n" +
                        "○アレルギーテスト済み※1 ○乾燥性敏感肌の方の協力によるパッチテスト済み※1\n" +
                        "[パッチテスト:皮膚に対する刺激性を確認するテストです]\n" +
                        "※1すべての方にアレルギーや皮膚刺激が起こらないというわけではありません。\"",
                "ingredientIDs" to listOf(
                    "40060",
                    "40444",
                    "40135",
                    "40016",
                    "40325",
                    "40386",
                    "40010",
                    "40155",
                    "40201",
                    "40037"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ボディ スムーザー AC",
                "brandId" to "30055",
                "categoryId" to "20015",
                "description" to "\"大人気の『ボディ スムーザー』から明るく心浮き立つ「アプリコットローズの香り」が登場。\n" +
                        "天然温泉水(角質柔軟成分)を含むスクラブが、固くざらついた古い角質をやわらかくして取り除き、全身をつるつる、すべすべのなめらかなお肌に整えます。\n" +
                        "うるおいに満ちた美肌に導く、”ローズ由来の美容成分”ガリカバラ花エキス・センチフォリアバラ花エキス・イザヨイバラエキス・発酵ローズハチミツ(グルコノバクター/ハチミツ発酵液)(すべて保湿成分)を配合。\n" +
                        "アプリコット色のローズをイメージし、華やかでやわらかい印象のローズに、みすみずしいアプリコットの甘さを添えた、心浮き立つフルーティーなローズの香りです。\n" +
                        "無着色・無鉱物油・パラベンフリー・アルコール(エタノール)フリー。\"",
                "ingredientIDs" to listOf(
                    "40028",
                    "40026",
                    "40473",
                    "40462",
                    "40460",
                    "40123",
                    "40217",
                    "40066",
                    "40140",
                    "40419"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "爽快洗口液オクチレモン",
                "brandId" to "30032",
                "categoryId" to "20015",
                "description" to "\"●うがいのあとに\n" +
                        "●10秒のうがいで速攻リフレッシュ\n" +
                        "●携帯に便利なスティックタイプ\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40060",
                    "40440",
                    "40127",
                    "40442",
                    "40412",
                    "40227",
                    "40405",
                    "40382"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ボディミルク ブライトニング&エイジングケア",
                "brandId" to "30061",
                "categoryId" to "20015",
                "description" to "\"「保湿・ブライトニング・エイジングケア」3つの機能を1本で叶える無添加の薬用ボディミルク。\n" +
                        "乾燥しやすく、日々の摩擦によるダメージでバリア機能が低下しがちなボディを、ハリとうるおいに満ちた透明肌に導きます。\n" +
                        "なめらかにのびて肌になじみやすく、ベタつき感もありません。\n" +
                        "(医薬部外品)\"",
                "ingredientIDs" to listOf(
                    "40247",
                    "40457",
                    "40010",
                    "40467",
                    "40236",
                    "40399",
                    "40331",
                    "40170",
                    "40439",
                    "40254"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リポソーム アドバンスト リペアセラム",
                "brandId" to "30041",
                "categoryId" to "20017",
                "description" to "\"●生体組成成分リン脂質から成り、たまねぎ状に幾重にも重なる層の中に美容成分を贅沢に抱えた0.1ミクロン※1の超微細なマイクロカプセル「新・多重層バイオリポソーム」が、1滴に1兆個※2。つけた瞬間から、成りかわるように肌に溶け込み、カプセルそのものがダイレクトに肌を美しくすることで、潤いに満ちたハリ・ツヤあふれる若々しい印象に導く、新・リポソーム美容液です。\n" +
                        "●角層深くまで浸透し、ぴったりと密着。カプセルが少しずつほぐれるように、じっくりと長時間、潤いと美容成分を届けます。\n" +
                        "●乾燥小ジワを目立たなくし※3、乱れたキメも美しく整え、毛穴レスな、なめらかな肌へ。\n" +
                        "●長時間潤いが持続。低湿度の過酷な環境下でも潤いを保持し、あらゆる肌不調を未然に防ぎ、乾燥などの外的ストレスに負けない健やかな肌に導きます。\n" +
                        "●後から使う化粧品のなじみを良くするブースティング効果があります。\n" +
                        "●リッチな保湿感がありながらも、べたつかない、みずみずしいオイルフリー処方。\n" +
                        "●乾燥肌にも深く優しくなじむ、低刺激処方です。\n" +
                        "●肌と心を癒すような、繊細で心地よいティーグリーンフローラルの香り。穏やかな気持ちから前向きな気持ちへと導かれるような変化を楽しめます。\n" +
                        "\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40135",
                    "40454",
                    "40016",
                    "40022",
                    "40050",
                    "40084",
                    "40186",
                    "40215"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リポソーム アドバンスト リペアクリーム",
                "brandId" to "30041",
                "categoryId" to "20017",
                "description" to "\"睡眠中の美を科学　一夜で美肌へ。1兆個※1のナイトカプセルが肌をかけめぐり、濃密なハリ・ツヤ・弾力。睡眠不足でも、3時間多く眠ったような肌へ\n" +
                        "\n" +
                        "※1回の使用量1gとして算出(概算値)\n" +
                        "\n" +
                        "「ナイト多重層バイオリポソーム」(ナイトカプセル)\n" +
                        "幾重にも重なった層の中に美容成分を贅沢に抱えた、生体組成成分でできた0.1~0.4ミクロンのマイクロカプセル。\n" +
                        "一層、一層、外側からじっくりとほぐれて、美を放ち続け、翌朝の肌に、押し返すようなハリと輝くツヤが目覚めます。\n" +
                        "塗布した瞬間から肌を美しく立て直し、シールド効果にも優れ、長時間潤いを逃がしません。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40452",
                    "40135",
                    "40377",
                    "40454",
                    "40180",
                    "40210",
                    "40054",
                    "40062"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "カプチュール トータル ル セラム",
                "brandId" to "30050",
                "categoryId" to "20017",
                "description" to "\"幹細胞研究(*1)20年以上のディオールから。\n" +
                        "肌にハリを与える美容液「カプチュール トータル ル セラム」誕生。\n" +
                        "進化したリポソーム、ダブル ベクター テクノロジーで、再生力の高い花・ロンゴザの発酵エキス(*2)を豊かに配合。たくましいハリのある肌へ導きます。\n" +
                        "処方の98%が自然由来成分(*3)。\n" +
                        "ディオールの花に関する高度な専門知識と、先進技術を駆使した成分を融合\n" +
                        "- マダガスカルのディオール ガーデンで収穫したロンゴザを発酵させて得たロンゴザの発酵エキス(*2)\n" +
                        "- イタリアのトスカーナ州フィレンツに新設されたディオール ガーデンに育つアイリスのエキス(*4)\n" +
                        "- 2種のヒアルロン酸(*5)と4種のポリグリセリン(*6)の複合成分からなるH.A. ポリフィラー テクノロジーが、長時間ハリとうるおいを高める効果をもたらします。\n" +
                        "ディオールは世界で初めて(*7)、リポソームを化粧品(初代カプチュール)に活用。\n" +
                        "\n" +
                        "(*1) 化粧品に応用するための幹細胞研究\n" +
                        "(*2)アフラモムムアングスチホリウム種子エキス、乳酸桿菌培養溶解質、酵母発酵エキス(整肌成分)\n" +
                        "(*3)自然由来指数98% (水66%を含む) ISO16128準拠\n" +
                        "(*4)イリス根エキス(整肌成分)\n" +
                        "(*5)ヒアルロン酸Na、アセチルヒアルロン酸Na(保湿成分)\n" +
                        "(*6)ポリグリセリン-3、ポリグリセリン-6(保湿成分)\n" +
                        "(*7)1986年に発売したカプチュールに活用、ディオール調べ\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40085",
                    "40010",
                    "40191",
                    "40326",
                    "40377",
                    "40347",
                    "40400",
                    "40331"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "レネルジー HPN クリーム",
                "brandId" to "30071",
                "categoryId" to "20017",
                "description" to "\"ゆるみ*1の兆しも現れも。\n" +
                        "いつでも上を向いていたいあなたへ\n" +
                        "300種のペプチドの恵みを。\n" +
                        "\n" +
                        "不可能を可能にする*2「レネルジー」から先進的エイジングケア*3クリーム、誕生。\n" +
                        "ランコム史上初、300種もの植物由来ペプチドの恵みを高濃度配合。\n" +
                        "ゆるみ*1の兆しも現れも、包括的にアプローチ。\n" +
                        "肌が自立するような密なハリ感で自信あふれる印象を上向かせてへ。\n" +
                        "\n" +
                        "*1肌のハリ感のなさのこと\n" +
                        "*2ランコムにおいて\n" +
                        "*3年齢に応じた、肌に潤いを与えるお手入れのこと。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40183",
                    "40210",
                    "40230",
                    "40468",
                    "40074",
                    "40268",
                    "40196",
                    "40194"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "カネボウ クリーム イン デイ",
                "brandId" to "30004",
                "categoryId" to "20017",
                "description" to "\"朝仕込んで、日中ずっとうるおいで満たす。\n" +
                        "夕方までのきれいは、KANEBOの朝クリームで。\n" +
                        "\n" +
                        "SPF20・PA+++で日中の紫外線を防ぐとともに乾燥から肌を守り、うるおいとハリを与える朝クリーム。赤ちゃんの未熟な肌を包むクリーム状の油、「胎脂」に着想を得て開発したクリーム処方「ベビーソフトオイル処方」採用で、長時間うるおいが持続。メイクのりを高めるだけでなく、乾燥による化粧くずれも防ぎます。朝にふさわしいみずみずしいテクスチャーで、内側からにじみ出るような自然なツヤ肌へ。\n" +
                        "\n" +
                        "日中の紫外線ダメージと乾燥の両方を防ぎ、美しいツヤ肌に\n" +
                        "SPF20・PA+++で日中の紫外線を防ぎながら、乾燥からも肌を守り、内側からにじみ出るような自然なツヤ肌へ導きます。化粧下地としてだけでなく、日中乾燥が気になる部分や、メイクの上からツヤを出したい部分に\"\"追い保湿\"\"する化粧上地としての使用もおすすめです。\n" +
                        "\n" +
                        "「ベビーソフトオイル処方」採用で、日中ずっとうるおい続く\n" +
                        "羊水中で赤ちゃんの肌を覆っているクリーム状の油、「胎脂」。羊水内では肌がふやけるのを防ぎ、空気中では乾燥から肌を守る、そんな相反する環境の変化から肌を守るためにつくられた保護膜。そんな胎脂ならではの保湿する力・皮膚を保護する力に着目し、開発したクリーム処方で長時間うるおいが持続。メイクのりを高めるだけでなく、日中も続くうるおいで、化粧くずれを防ぎます。\n" +
                        "\n" +
                        "朝のお手入れのためにつくられた、みずみずしいテクスチャー\n" +
                        "やわらかな感触で、心地よく肌に溶け込むような使い心地。さらに肌止まり良く、ファンデーションの上からでもよれずになめらかになじむテクスチャーにアレンジ。朝にふさわしいみずみずしい緑と花々をイメージしたフレッシュフローラルの香りで、気分まで高まります。\n" +
                        "\n" +
                        "※SPFとは紫外線B波から肌を守る効果を示す指数、PAとは紫外線A波から肌を守る効果を示す分類です。SPF、PA表示は国際的な基準で1cm²あたり2mg塗布して測定した値です。商品選択時の目安とお考えください。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40355",
                    "40016",
                    "40378",
                    "40219",
                    "40135",
                    "40170",
                    "40327",
                    "40191",
                    "40250"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "セラム シールド",
                "brandId" to "30008",
                "categoryId" to "20019",
                "description" to "\"根深い渇きに高保湿膜。うるおい改善+シワ改善。バーム状密封美容液。\n" +
                        "(医薬部外品)\"",
                "ingredientIDs" to listOf(
                    "40457",
                    "40467",
                    "40007",
                    "40375",
                    "40249",
                    "40429",
                    "40034",
                    "40012",
                    "40056",
                    "40335"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "薬用クリアバーム",
                "brandId" to "30023",
                "categoryId" to "20019",
                "description" to "\"有効成分で、メラニンの生成を抑え、「シミ・そばかす」を防ぐとともに、赤っぽくなりがちなニキビや肌あれを防ぎます。\n" +
                        "ひと肌でとろけてなめらかに広がり、べたつかず、みずみずしい美容クリームのような使い心地で、オールシーズン使えます。\n" +
                        "敏感肌の方でも毎日使える設計です。\n" +
                        "(医薬部外品)\"",
                "ingredientIDs" to listOf(
                    "40247",
                    "40138",
                    "40411",
                    "40457",
                    "40232",
                    "40375",
                    "40423",
                    "40008",
                    "40284",
                    "40034"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/6.jpg"
            ),
            mapOf<String, Any>(
                "name" to "RMK Wトリートメントオイル",
                "brandId" to "30009",
                "categoryId" to "20019",
                "description" to "\"RMK のスキンケアアイコン\n" +
                        "乾燥やハリ不足など、トータルにアプローチする\n" +
                        "オイル層とうるおい層、Wの効果のトリートメントオイル\n" +
                        "肌をやわらかくするオイル層と、角質層をみずみずしく満たすうるおい層がひとつになった、プレケア用のトリートメントオイル。スキンケアの最初に使うことで、その後に続く保湿液などが角質層に浸透しやすい状態にととのえます。\n" +
                        "油性と水性ならではのダブル作用で乾燥を防ぐラッピング効果を発揮し、肌のバリア機能をサポート。美容オイルやマッサージオイルとしても使え、血行が促され＊1、引きしまった肌に。\n" +
                        "保湿効果の高いアルガンオイル配合。リッチなのに、べたつかないテクスチャーと、オレンジやネロリなどの8種の精油をブレンドしたフローラルシトラスの香りが、肌をふんわり包み込みます。\"",
                "ingredientIDs" to listOf(
                    "40191",
                    "40135",
                    "40180",
                    "40183",
                    "40104",
                    "40322",
                    "40428",
                    "40444",
                    "40085",
                    "40010"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "薬用ナイトパック",
                "brandId" to "30023",
                "categoryId" to "20019",
                "description" to "\"○ 繰り返しがちな肌あれ・乾燥を防ぐ、夜の集中ケアパックです。\n" +
                        "○ やさしく肌に広がるクリーミーなバームのパックで、べたつかずに一晩中密封してうるおいが続き、翌朝なめらかな肌に整えます。\n" +
                        "○クリームのなめらかさとバームの密封力を実現したハイブリッドケア処方。\n" +
                        "○ 蓄積した、乾燥など日中に受けた肌ダメージ対策に、塗って寝るだけの簡単ケア\n" +
                        "(医薬部外品)販売名:イハダ 薬用ナイトバーム\"",
                "ingredientIDs" to listOf(
                    "40247",
                    "40138",
                    "40052",
                    "40299",
                    "40011",
                    "40046",
                    "40457",
                    "40467",
                    "40182",
                    "40008"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アミノモイスト エイジングケア オイル ",
                "brandId" to "30066",
                "categoryId" to "20019",
                "description" to "\"乾燥やハリ不足が気になる肌に。ふっくら実感。濃密な1滴\n" +
                        "・浸透※1型アミノ酸系オイ※2を最高濃度で配合(シリーズ内比)\n" +
                        "・角質層のすみずみまで浸透。セラミドの働きを補いうるおいを与えてケア\n" +
                        "・乾燥やカサつきが気になるときは、乳液クリーム※3と混ぜて使うのがおすすめ\n" +
                        "\n" +
                        "\n" +
                        "●無香料・無着色　●アルコール(エチルアルコール)無添加　●パラベンフリー\n" +
                        "●紫外線吸収剤フリー　●アレルギーテスト済み※4　●パッチテスト済み※5\n" +
                        "●スティンギングテスト済み※5(ピリピリ、ヒリヒリといった使用直後の刺激感を確かめるテストです)　\n" +
                        "●敏感肌・乾燥肌の方による連用テスト済み※5\n" +
                        "\n" +
                        "※1 角質層まで\n" +
                        "※2 ラウロイルグルタミン酸ジ(フィトステリル/オクチルドデシル):保湿\n" +
                        "※3 同エイジングケアラインの乳液クリームと美容液オイルに限る\n" +
                        "※4 すべての方にアレルギーが起こらないというわけではありません\n" +
                        "※5 すべての方の肌に合うということではありません\n" +
                        "エイジングケア:年齢に応じたお手入れ\"",
                "ingredientIDs" to listOf(
                    "40278",
                    "40068",
                    "40077",
                    "40139",
                    "40345",
                    "40244",
                    "40444",
                    "40119",
                    "40010",
                    "40059"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "オイデルミン エッセンスローション",
                "brandId" to "30011",
                "categoryId" to "20022",
                "description" to "\"あなたの美しさを日々、更新。　生命感あふれるつややかな肌が続く。\n" +
                        "今まで発揮しきれていない、美の回復に着目。\n" +
                        "乾燥や環境変化に負けない、美しい肌サイクルを目指して、悪いものを取り除き、良いものを取り入れる習慣を。\n" +
                        "角層のすみずみまで素早く浸透し、乾燥による小ジワを目立たなくします。*\n" +
                        "輝くような透明感と、うるおったなめらかな肌へ導きます。　　\n" +
                        "*効能評価試験済み\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40085",
                    "40016",
                    "40010",
                    "40135",
                    "40170",
                    "40379",
                    "40466",
                    "40030",
                    "40180"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "メラノフォーカスEV",
                "brandId" to "30056",
                "categoryId" to "20022",
                "description" to "\"シミができる肌特有のダメージ状態を徹底的に研究。　\n" +
                        "シミの原因に根本アプローチ*。　\n" +
                        "しっかり肌奥に届く2種の美白有効成分がメラニンの生成を抑え、シミ・そばかすを防ぎます。　\n" +
                        "肌なじみに優れたHAKUの独自処方。のびのよいなめらかな感触で、溶けるようになじむ薬用 美白美容液。　\n" +
                        "「美発光肌」で輝き続ける。\n" +
                        "\n" +
                        "*メラニンの生成を抑え、シミ・そばかすを防ぐ\n" +
                        "(医薬部外品)\"",
                "ingredientIDs" to listOf(
                    "40009",
                    "40247",
                    "40127",
                    "40392",
                    "40083",
                    "40470",
                    "40020",
                    "40052",
                    "40101",
                    "40079"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アドバンス ナイト リペア SMR コンプレックス",
                "brandId" to "30028",
                "categoryId" to "20022",
                "description" to "\"肌のリズムに寄り添い、昼は環境要因による乾燥ダメージから肌を守り、夜間はうるおいを与えて、毎日、フレッシュでなめらかな肌へ導く美容液です。素早く肌になじみ、うるおいがすみずみ*1まで浸透。使うたびに、生命感あふれるように輝き、若々しい印象のすこやかな美しさへ。\n" +
                        "*1 角質層まで\n" +
                        "\n" +
                        "1滴の力\n" +
                        "素早く肌になじみ、うるおいがすみずみまで*1浸透。\n" +
                        "目指したのは、ファスト リペア*2\n" +
                        "*1角質層まで\n" +
                        "*2保湿もハリ・ツヤも与える手軽なトータルケア\n" +
                        "\n" +
                        "1本の感動\n" +
                        "使うたびに、生命感あふれるような輝き。\n" +
                        "若々しい印象のすこやかな美しさを、その肌に。\n" +
                        "\n" +
                        "独自成分クロノラックス(TM) パワーテクノロジー*3を配合\n" +
                        "肌は遺伝より環境や食生活、ライフスタイルの影響の方が大きいことから、エスティローダーは毎日のスキンケアの重要性に注目しました。まずは、環境ストレスが与える乾燥などの肌ダメージを溜めこまないこと。独自成分、クロノラックス(TM) パワー テクノロジーを配合した「アドバンス ナイト リペア」は、肌本来の働きをサポートし、ファストリペア*で、肌にみずみずしいうるおいとハリをもたらします。毎日続けることで、使うたびに、生命感あふれるような輝き。若々しい印象のすこやかな美しさを肌に届けます。\n" +
                        "*3 肌は遺伝より環境や食生活、ライフスタイルの影響の方が大きいことから、毎日のスキンケアの重要性に注目した、うるおいを与え肌をすこやかに保つエスティローダー独自のテクノロジー\n" +
                        "\n" +
                        "肌のリズムに合わせたお手入れに着目した美容液\n" +
                        "すこやかな肌には、日中ダメージから守り、夜間に修復して備えるというリズムがあります。そんな肌本来が持つ24時間のリズムに合わせたお手入れに着目した美容液。アドバンス ナイト リペアは、昼は環境要因による乾燥ダメージから肌を守り、夜はたっぷりなうるおいを与えてケアします。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40310",
                    "40032",
                    "40322",
                    "40301",
                    "40371",
                    "40031",
                    "40136",
                    "40010",
                    "40135"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "カネボウ ヴェイル オブ デイ",
                "brandId" to "30004",
                "categoryId" to "20022",
                "description" to "\"日中の肌の乾燥を防ぎながら、強力な紫外線から守る。\n" +
                        "肌に補水し続けるウォーターサプライUV美容液。\n" +
                        "水相成分約75%を含む美容液が厚みのあるみずみずしい水膜ヴェイルを形成。\n" +
                        "強力な紫外線をカットしながら、肌に補水し続けるウォーターサプライUV美容液。\n" +
                        "厚みのあるみずみずしい水膜ヴェイルが心地よく肌を包み込み、生命力あふれるようなツヤのある肌仕上がりへ。\n" +
                        "化粧下地としても使え、乾燥による化粧くずれを防ぎ、夕方までうるおいに満ちたなめらかな肌が続く。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40378",
                    "40183",
                    "40212",
                    "40327",
                    "40135",
                    "40314",
                    "40253",
                    "40010",
                    "40302"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リードルショット100",
                "brandId" to "30014",
                "categoryId" to "20022",
                "description" to "\"毎日塗って変わる。\n" +
                        "よりなめらかな素肌へ。今日から美容針*活!\n" +
                        "肌表面の不要な角質を整えることでキメ細かいなめらかな素肌へ整えます。\n" +
                        "*シリカ(スクラブ)\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40016",
                    "40135",
                    "40268",
                    "40010",
                    "40357",
                    "40006",
                    "40288",
                    "40337",
                    "40187"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "スペシャルジェルクリーム EX (ブライトニング)",
                "brandId" to "30015",
                "categoryId" to "20023",
                "description" to "\"じゅわ～っと浸透*、ふんわり密封。うるっと明るい透明美肌。濃密ジェルクリームがキメの奥までじゅわ～っと浸透。\n" +
                        "うるおって、明るく透明感のある肌へ。美白ケアのためのオールインワン。美白とは、メラニンの生成を抑え、シミ・そばかすを防ぐこと。\n" +
                        "*角層まで\n" +
                        "(医薬部外品)\n" +
                        "※こちらの商品は詰め替えのため、ケースは付属いたしません。\"",
                "ingredientIDs" to listOf(
                    "40009",
                    "40441",
                    "40011",
                    "40013",
                    "40014",
                    "40426",
                    "40221",
                    "40427",
                    "40455",
                    "40052"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ハトムギ保湿ジェル",
                "brandId" to "30053",
                "categoryId" to "20023",
                "description" to "\"香料や着色料など余計なものは一切使用しないシンプルな処方。 天然植物由来の保湿成分「ハトムギエキス」を配合し、べたつかないのにしっかり潤う保湿ジェルです。\n" +
                        "\n" +
                        "水分を限界までたっぷりと含んだ、とろけるように滑らかでみずみずしい感触のジェルが、肌にのせた瞬間すっと素早く浸透。※ 肌のすみずみまで水分補給しながら、肌の上にもしっかりとどまり、油分に頼らずにうるおいをキープします。\n" +
                        "\n" +
                        "原料となる「ハトムギエキス」は、肌にうるおいを与えるだけでなく、美肌をサポートする力を持つ成分と言われているため気になる肌トラブルもケアしてくれます。\n" +
                        "\n" +
                        "[ こんな方にオススメ ]\n" +
                        "\n" +
                        "・手軽に保湿ケアをしたい方に\n" +
                        "・べたつきが苦手な方に\n" +
                        "・乾燥などの肌トラブルに悩む方に\n" +
                        "・惜しみなくたっぷり使いたい方に\n" +
                        "・家族みんなで使いたい方に\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40135",
                    "40016",
                    "40183",
                    "40238",
                    "40243",
                    "40282",
                    "40010",
                    "40024",
                    "40447"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アスタリフト オプミー ",
                "brandId" to "30016",
                "categoryId" to "20023",
                "description" to "\"時間はないけど、肌のお手入れの質は落とせないいつも「私」を大切にしたい全ての女性のためにオプミーは生まれました。\n" +
                        "\n" +
                        "忙しいときは、これだけで4つの役割。\n" +
                        "「化粧水」「美容液」「乳液」「クリーム」\n" +
                        "\n" +
                        "こだわったのは、高い保湿力*3と持続力*4。\n" +
                        "\n" +
                        "1. 「高濃度*3ピュアコラーゲン*5」などの美容成分が、うるおいに満ちた肌へ誘う。\n" +
                        "2. ナノ化したビタミンA*6、ビタミンE*7が高保湿をサポート。\n" +
                        "3. 独自開発のジェルネットワーク処方で、うるおいを閉じ込める。\n" +
                        "\n" +
                        "さらには、うるおうのにベタつかないテクスチャーだから、\n" +
                        "お化粧前に使用しても、すぐにメイクできる。忙しい朝にも最適。\n" +
                        "\n" +
                        "もっとシンプルに、わたしらしく。\n" +
                        "*1 使用目安のこと。*2 当社比。\n" +
                        "*3 当社比。 *4 朝塗布してから夕方洗顔するまで。 *5 加水分解コラーゲン(うるおい成分)。 *6 パルミチン酸レチノール(うるおい成分)。 *7 トコフェロール(うるおい成分)。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40010",
                    "40135",
                    "40016",
                    "40183",
                    "40326",
                    "40253",
                    "40411",
                    "40148",
                    "40291"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "プレミアリフト",
                "brandId" to "30001",
                "categoryId" to "20023",
                "description" to "\"シワ改善有効成分配合。ハリ肌の土台*1に着目。\n" +
                        "ハリ、シワ対策に。潤い・ハリ巡る薬用オールインワン\n" +
                        "\n" +
                        "●薬用有効成分ナイアシンアミドがシワを改善し、ハリ・弾力満ちる\n" +
                        "●デリバリーナノカプセル*2が肌の潤いを巡らせ*3、もっちりみずみずしい肌へ\n" +
                        "●天然精油によるカーミングフローラルの香り\n" +
                        "\n" +
                        "*1 角質層\n" +
                        "*2 ビタミンA油、天然ビタミンE、テトラ2-ヘキシルデカン酸アスコルビル(エモリエント成分)\n" +
                        "*3 角質層まで\n" +
                        "\n" +
                        "(医薬部外品)\n" +
                        "販売名:薬用プレミアリフト\"",
                "ingredientIDs" to listOf(
                    "40268",
                    "40138",
                    "40154",
                    "40283",
                    "40103",
                    "40380",
                    "40204",
                    "40276",
                    "40281",
                    "40084"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "パーフェクトエッセンス",
                "brandId" to "30069",
                "categoryId" to "20023",
                "description" to "\"今あるシミ・シミ予備軍にも美白有効成分がダイレクトに発揮。\n" +
                        "化粧水・美容液・乳液1品3役の薬用美白パーフェクトエッセンスです。\n" +
                        "さらりとのび広がり、みずみずしくやさしい使い心地。\n" +
                        "お得なつめかえ用。\"",
                "ingredientIDs" to listOf(
                    "40268",
                    "40457",
                    "40007",
                    "40467",
                    "40018",
                    "40299",
                    "40407",
                    "40410",
                    "40456",
                    "40045"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "エファクラ ピールケア セラム",
                "brandId" to "30070",
                "categoryId" to "20024",
                "description" to "\"肌トラブルに角質ケア。\n" +
                        "触りたくなるような肌質、毛穴レス印象肌へ\n" +
                        "\n" +
                        "古い角質は、肌のざらつきだけでなく、毛穴の開きや黒ずみ・にきびにまで発展してしまう可能性もあるという事をご存知でしたか?\n" +
                        "皮膚科学に基づいて開発した、敏感肌*1にも使える角質ケア美容液。\n" +
                        "角質の表面・内部へのトータルアプローチで、触りたくなるような肌質、毛穴レス印象肌に導きます。毎日のスキンケアルーティーンに取り入れやすい、アジア人の肌ニーズを考慮した心地よいフォーミュラです。\n" +
                        "\n" +
                        "角質層の表面・内部へトータルアプローチ\n" +
                        "独自成分*2トリアシッドコンプレックス*3を配合。肌表面の古くなった角質を柔らかくして、キメのととのった毛穴レス印象肌に導きます。さらに、ナイアシンアミド*4とターマルウォーター*5が角質層まで浸透。うるおいを与えながら、肌をすこやかに保ちます。\n" +
                        "\n" +
                        "アジア人の敏感肌*1を考慮して開発\n" +
                        "20～40代のアジア人女性に多く見られる肌トラブル*6の要因となる古い角質に注目。\n" +
                        "\n" +
                        "・低刺激設計*7\n" +
                        "・アレルギーテスト済み*8\n" +
                        "・にきびのもとになりにくい処方*9\n" +
                        "・アジア人の肌でテスト済み*1\n" +
                        "\n" +
                        "触りたくなるようなしっとりとした保湿感\n" +
                        "なじみやすく、べたつかないテクスチャーをもたらしました。キメをととのえて、つるんとなめらかな肌へ導きます。容器は、1回の容量を調整しやすいスポイトタイプを採用しています。\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40468",
                    "40135",
                    "40322",
                    "40268",
                    "40447",
                    "40304",
                    "40306",
                    "40307",
                    "40465"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リセットクリア N ",
                "brandId" to "30063",
                "categoryId" to "20024",
                "description" to "\"ひたして・うかせて・からめとる、ふきとり化粧液\n" +
                        "\n" +
                        "肌に積もる不要なものをとり去る、ふきとり化粧液。\n" +
                        "きちんとスキンケアしていても肌につもる老廃物を浄化。ひたして・うかせて・からめとる、ふきとり化粧液です。放っておくとサビのように残る皮脂や、浸透*1・透明感の妨げとなる不要な角層をやさしく除去します。天然精油による、爽やかなハーバル・フローラルの香りです。\n" +
                        "\n" +
                        "コットンとのあわせ使いで\"\"ひたして・うかせて・からめとる\"\"\n" +
                        "化粧液をコットンに含ませて肌表面をひたすことで、不要な汚れ*2をうかせ、ふきとることでからめとり、すっきりつるつる・すべすべのクリアな肌へ。その後のスキンケアの浸透*1感がよい肌に整えます。\n" +
                        "\n" +
                        "美しさをサポートするパワーボタニカル成分*3を配合\n" +
                        "ベネフィークは東洋の知恵に学び研究を深め、肌本来の美しさの力に着目。その研究により、大地からの恵みを選りすぐったパワーボタニカル成分*3を配合。うるおいで満たすことで、明るい透明感のある肌へと導きます。\n" +
                        "\n" +
                        "爽やかなハーバル・フローラルの香り、とろみのあるやさしいテクスチャー\n" +
                        "とろみのあるテクスチャーのため肌あたりがやさしく、みずみずしさあふれる使用感も特長。天然精油配合による爽やかなハーバル・フローラルの香りで、心地よいスキンケアタイムを演出します。\n" +
                        "\n" +
                        "*1 角層まで\n" +
                        "*2 不要な角層や酸化した皮脂など\n" +
                        "*3 ケイヒエキス・トウキエキス・ジオウエキス・グリセリン:保湿\"",
                "ingredientIDs" to listOf(
                    "40010",
                    "40039",
                    "40085",
                    "40124",
                    "40135",
                    "40444",
                    "40066",
                    "40044",
                    "40241",
                    "40113"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ネクターデルミエール アクティベーターオイルウォーター",
                "brandId" to "30068",
                "categoryId" to "20024",
                "description" to "\"天然由来のAHA(*1)とPHA(*2)を配合した「角質ケアできるブースター」。\n" +
                        "つけてすぐなじむ水分と油分の黄金比率を採用したオイルウォーターが肌をなめらかにして、\n" +
                        "うるおいで満たし、きめを整え、毛穴を目立たなくし、くすみ(*3)までケアします。\n" +
                        "角質にアプローチする「浸透(*4)美肌水」で、触れたくなるようなつるんとなめらかな肌へ。\n" +
                        "\n" +
                        "*1:グレープフルーツ果実エキス(整肌成分)\n" +
                        "*2:グルコノラクトン(整肌成分)\n" +
                        "*3:乾燥や古い角質による\n" +
                        "*4:角質層まで\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40312",
                    "40096",
                    "40135",
                    "40141",
                    "40331",
                    "40143",
                    "40175",
                    "40388",
                    "40311"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "ビューティクリア ブラックスクラブウォッシュ",
                "brandId" to "30044",
                "categoryId" to "20024",
                "description" to "\"磨き上げスクラブ洗顔料 毛穴の中の汚れ&古い角質も除去 毛穴より小さく崩れるスクラブ配合\n" +
                        "\n" +
                        "磨き上げスクラブ洗顔 毛穴より小さく崩れるスクラブ(黒スクラブ・柚子スクラブ)※1・炭※2・泥※3配合で、毛穴の中の汚れ・角栓・古い角質・皮脂を吸着磨き上げ洗浄。\n" +
                        "洗いあがりは澄みわたるツヤ肌に。\n" +
                        "爽やかなミントウォーターの香り。\n" +
                        "\n" +
                        "毎日お使いいただけます。\n" +
                        "毛穴汚れが気になる時は、30秒パックして洗い流す毛穴パックとしてもおすすめ。\n" +
                        "※1 洗浄成分:セルロース、エチルセルロース、酸化鉄、ユズ果実\n" +
                        "※2 洗浄成分\n" +
                        "※3 洗浄成分:モロッコ溶岩クレイ、ホワイトクレイ(カオリン)\"",
                "ingredientIDs" to listOf(
                    "40135",
                    "40362",
                    "40444",
                    "40446",
                    "40285",
                    "40026",
                    "40028",
                    "40016",
                    "40393",
                    "40197"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "フェイススクラブ",
                "brandId" to "30003",
                "categoryId" to "20024",
                "description" to "\"独自成分アパタイトをスクラブ粒子化。肌のざらつきや毛穴汚れの気になる方に。\n" +
                        "\n" +
                        "ハップアールの独自成分、スキンケア用アパタイト*1をスクラブ粒子化して、たっぷりと配合したフェイススクラブです。\n" +
                        "つぶつぶのアパタイトが、ざらつきや毛穴汚れ、くすみ*2の原因となる酸化した皮脂や古い角質などを選んで吸着します。一方で、うるおいに必要な皮脂は吸着せずに残します。\n" +
                        "心地よいつぶつぶ感のために、スクラブ粒子は丸い粒状につくりあげました。\n" +
                        "やさしくクルクルした後は、思わず触れたくなるスベスベ肌に。\n" +
                        "*1:ヒドロキシアパタイト(洗浄補助成分)\n" +
                        "*2:汚れや古い角質による\n" +
                        "\n" +
                        "≪こんなお悩みに≫\n" +
                        "●肌がざらざら、ごわごわ。特に、マスクを外した後のあご周りなどのざらつきが気になる。\n" +
                        "●朝、メイクのノリが悪い。化粧水などの保湿ケアが肌になじみにくい、メイクのノリが悪いと感じる。\n" +
                        "●毛穴が気になる。小鼻の黒ずみ毛穴や、皮脂が多いエリアの毛穴の目立ちが気になる。\n" +
                        "●肌がくすんで見える。もっと透明感がほしい!\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40305",
                    "40135",
                    "40197",
                    "40010",
                    "40193",
                    "40147",
                    "40030",
                    "40149",
                    "40203"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "キス シュガー スクラブ",
                "brandId" to "30074",
                "categoryId" to "20025",
                "description" to "\"シュガースクラブでやさしく角質ケア\n" +
                        "洗い流さないリップスクラブ\n" +
                        "\n" +
                        "・やさしく角質ケア\n" +
                        "シュガーのように溶けるスクラブで唇の角質をやさしくケア。\n" +
                        "・うるおい保湿\n" +
                        "3種のフルーツオイル*でくちびるを乾燥からケア。\n" +
                        "・ミントの香り\n" +
                        "ほんのり甘く、さわやかなシュガーミントの香り。\n" +
                        "・つるるんリップに\n" +
                        "日中だけでなく、ナイトケアにも!\n" +
                        "なめらかで、ぷるんとしたくちびるに。\n" +
                        "\n" +
                        "*ザクロ種子油、ヨーロッパキイチゴ種子油、ブドウ種子油\"",
                "ingredientIDs" to listOf(
                    "40351",
                    "40411",
                    "40190",
                    "40133",
                    "40272",
                    "40284",
                    "40099",
                    "40187",
                    "40188",
                    "40338"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "グラスティングメルティングバーム",
                "brandId" to "30010",
                "categoryId" to "20025",
                "description" to "\"なめらかで軽い塗り心地でもっちり潤う水膜リップバーム\n" +
                        "鮮やかで多彩なカラー構成で植物性保湿オイルが唇をしっとりもっちりと保湿してくれます。\n" +
                        "\n" +
                        "なめらかなタッチで唇にぴったりフィットします。\n" +
                        "水分が飛ばない比率が塗り重ねても透明感のあるリップバームを演出します。\n" +
                        "\n" +
                        "やわらかな血色感あるアプリコットベージュ\"",
                "ingredientIDs" to listOf(
                    "40406",
                    "40252",
                    "40303",
                    "40284",
                    "40263",
                    "40235",
                    "40354",
                    "40070",
                    "40006",
                    "40174"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "プランプリップケアスクラブ",
                "brandId" to "30034",
                "categoryId" to "20025",
                "description" to "\"塗るだけで手軽に「柔らか唇エステ」♪潤いケアと角質ケアが1本で簡単に出来るリップスクラブ。\n" +
                        "\n" +
                        "唇の上で溶けるお砂糖(※スクロース:スクラブ剤)のスクラブが古い角質をOFF!\n" +
                        "プランパー効果も兼ね備えているので、「ガサガサ」「くすみ」をケアしてなめらかでふっくらとした唇に仕上げます(※メイク効果による)。\n" +
                        "\n" +
                        "まるでリップパックした後のような保湿効果が持続。デイケア/ナイトケアどちらにもおすすめ。\"",
                "ingredientIDs" to listOf(
                    "40451",
                    "40411",
                    "40104",
                    "40164",
                    "40190",
                    "40338",
                    "40406",
                    "40180",
                    "40184",
                    "40129"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "リップスリーピングマスク",
                "brandId" to "30005",
                "categoryId" to "20025",
                "description" to "\"寝ている間に唇の角質をケア\n" +
                        "翌朝、理想のリップコンディションに仕上げるリップスリーピングマスク\n" +
                        "\n" +
                        "1)「ベリーフルーツコンプレックス(Berry Fruit Complex)」配合\n" +
                        "気になる角質や乾燥にアプローチが期待できる「ベリーフルーツコンプレックス* 」が、なめらかでふっくらとした唇に仕上げます。リップメイク前に使用することで、リップの発色や密着力が高まります。\n" +
                        "\n" +
                        "2)乾燥した唇を寝ている間に集中うるおいケア\n" +
                        "うるおい保護成分・ココナッツオイル(ヤシ油)を配合し、睡眠中に水分が蒸発し乾燥した唇をしっとりケアします。\n" +
                        "\n" +
                        "3)とろっとしたテクスチャーで唇に密着\n" +
                        "濃厚なテクスチャーで塗り直さなくても唇にぴたっと密着します。付属のスパチュラを使用することで唇に均一に塗ることが出来ます。\n" +
                        "\n" +
                        "4)5種類の香りで、寝ている間も心地よくケア\n" +
                        "ベリー/グレープフルーツ/バニラ/スイートキャンディー/グミベアの5種類の香りをお好みでお楽しみいただけます。\n" +
                        "\n" +
                        "*ザクロ果汁、キイチゴ果汁、ブドウ果汁\"",
                "ingredientIDs" to listOf(
                    "40406",
                    "40451",
                    "40450",
                    "40351",
                    "40354",
                    "40159",
                    "40432",
                    "40264",
                    "40126",
                    "40156"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "タカミリップ",
                "brandId" to "30048",
                "categoryId" to "20025",
                "description" to "\"肌のお手入れは入念にしている女性でも忘れがちな唇のスキンケア。\n" +
                        "保湿や荒れ予防だけでなくエイジングケア※2 や透明感※3など美しさにもこだわった処方を追求。\n" +
                        "また、無防備で繊細な部位であることから、より丁寧なケアが必要だと考えた結果「1日10回※1の唇スキンケア習慣」を提唱。\n" +
                        "まさに“ありそうでなかった新発想の唇用美容液です。\"\"\"",
                "ingredientIDs" to listOf(
                    "40360",
                    "40354",
                    "40451",
                    "40253",
                    "40294",
                    "40159",
                    "40178",
                    "40139",
                    "40060",
                    "40145"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アロマボディシート",
                "brandId" to "30020",
                "categoryId" to "20026",
                "description" to "\"みずみずしい感触で、ひんやりリフレッシュできるアロマボディシート。大判で厚みのあるシートだから、1枚で全身すっきり。\n" +
                        "さらりとした肌が続きます。持ち運びに便利な15枚入り。\n" +
                        "シートサイズ:180mm×200mm\"",
                "ingredientIDs" to listOf(
                    "40444",
                    "40085",
                    "40010",
                    "40081",
                    "40216",
                    "40316",
                    "40082",
                    "40040",
                    "40131",
                    "40132"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "薬用ソフトストーンW",
                "brandId" to "30051",
                "categoryId" to "20026",
                "description" to "\"薬用ソフトストーンW(医薬部外品)\n" +
                        "\n" +
                        "●新処方 防臭効果とさらさら感がアップ。6年連続売上No.1(※)\n" +
                        "●制汗デオドラントワキの汗、ニオイを元から防ぐ 直(ジカ)ヌリスティック。無香料、無着色、アルコールフリー。\n" +
                        "●天然アルム石に着目した、有効成分「焼ミョウバン」配合。\n" +
                        "●朝ぬって夜までつづいちゃう。\n" +
                        "※…インテージSRI 2013年5月から2021年6月 ソフトストーンW(販売名…デオナチュレソフトストーンEX)累計販売金額。\n" +
                        "\n" +
                        "※パッケージ、成分等は予告なく変更となる場合がございます。\"",
                "ingredientIDs" to listOf(
                    "40437",
                    "40075",
                    "40171",
                    "40192",
                    "40038",
                    "40206",
                    "40449",
                    "40382",
                    "40464",
                    "40475"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "アンティ パースパイラント デオドラント ロールオン",
                "brandId" to "30036",
                "categoryId" to "20026",
                "description" to "\"2010年@cosmeベストコスメ大賞 ボディケア部門 第3位2009年@cosmeベストコスメ大賞 ボディケア部門 第3位2007年@cosmeベストコスメ大賞 ボディケア部門 第3位\n" +
                        "\n" +
                        "気になるニオイや脇汗、ベタつき。暑い夏でも驚くほどのサラサラ感は一度使うとやみつきに。\n" +
                        "\n" +
                        "名品の多いクリニーク製品の中でも、「毎年リピートして何本使ったかわからない!」「他のデオドラントに浮気しても、やっぱり戻ってきてしまう。」など、@cosmeメンバーさんの中でも圧倒的な人気を誇るクリニークのアンティ パースパイラント デオドラント ロールオン N。\n" +
                        "\n" +
                        "ロールオンで簡単に脇に塗れるだけでなく、500円玉大のボールは脇の曲線にぴったりフィットし塗り心地も◎。さらに、とろみのあるテクスチャーは液だれしにくくしっかりと肌に密着します。\n" +
                        "\n" +
                        "@cosmeでは毎年暑い時期になると、さらに注目が高まる夏のロングセラーデオドラントです。\"",
                "ingredientIDs" to listOf(
                    "40065",
                    "40457",
                    "40342",
                    "40343",
                    "40341",
                    "40092",
                    "40417",
                    "40418",
                    "40181",
                    "40476"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "薬用足指さらさらクリーム",
                "brandId" to "30051",
                "categoryId" to "20026",
                "description" to "\"販売名:DN薬用フットクリームFc(医薬部外品)\n" +
                        "\n" +
                        "●新処方、防臭効果とさらさら感アップ。足用制汗剤クリーム。\n" +
                        "●足ムレ、ニオイに深く悩む方に。足用処方の直(ジカ)ヌリ制汗剤クリームタイプ。\n" +
                        "●無香料、無着色。\n" +
                        "●ニオイの発生源の足指の間に、しっかりぬり込めるから、足特有のムレ、ニオイをしっかり防ぐ。\n" +
                        "●天然アルム石に着目した、有効成分「焼ミョウバン」配合。\n" +
                        "●汗吸収パウダー(無水ケイ酸)、ローズマリーエキス&ティーツリーハーブ(保湿成分)配合。\n" +
                        "\n" +
                        "※パッケージ、成分等は予告なく変更となる場合がございます。\"",
                "ingredientIDs" to listOf(
                    "40437",
                    "40075",
                    "40171",
                    "40255",
                    "40470",
                    "40354",
                    "40425",
                    "40336",
                    "40374",
                    "40477"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
            mapOf<String, Any>(
                "name" to "パウダースプレー(無香性) ",
                "brandId" to "30026",
                "categoryId" to "20026",
                "description" to "\"○ニオイ菌を殺菌※1。ワキ・首筋・胸元・背中など、全身の汗のニオイや体臭をしっかり防ぐ\n" +
                        "○汗を抑える汗取りパッド効果　汗吸着パウダー/みょうばん(制汗成分)\n" +
                        "○シュッとひと吹きでさらさらの防臭ヴェールを形成\n" +
                        "汗をかいても流れにくく、動いても落ちにくい処方で肌にピッタリ密着\n" +
                        "○汗臭はもちろん、ストレス臭※2までケア\n" +
                        "○ヒアルロン酸パウダー(保湿)配合\n" +
                        "○銀含有アパタイト(さらさらパウダー)配合\n" +
                        "\n" +
                        "※1 有効成分:IPMP(イソプロピルメチルフェノール)\n" +
                        "※2 ストレス臭を包み込んで嫌なニオイを目立たなくするSTハーモナージュ香料配合\"",
                "ingredientIDs" to listOf(
                    "40075",
                    "40437",
                    "40055",
                    "40299",
                    "40430",
                    "40467",
                    "40414",
                    "40469",
                    "40226",
                    "40374"
                ),
                "imageURL" to "https://storage.cloud.google.com/purelab/7.jpg"
            ),
        )


        return list
    }

    /** ブランド追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupBrandSaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "ブランド名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveBrand(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "ブランド名を保存しました")
        }
    }


    /** カテゴリ追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupCategorySaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "カテゴリ名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveCategory(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "カテゴリ名を保存しました")
        }
    }


    /** 成分追加セクションの保存ボタンのクリックリスナーを設定 */
    private fun setupIngredientSaveButton(binding: AdminInputSectionBinding) {
        binding.saveButton.setOnClickListener { v ->
            val name: String = binding.inputField.text.toString()
            if (name.isEmpty()) {
                CustomSnackbar.showSnackBar(v, "成分名を入力してください")
                return@setOnClickListener
            }
            val product = hashMapOf("name" to name)
            vm.saveIngredient(product)

            // 保存完了のsnackbarを表示
            CustomSnackbar.showSnackBar(v, "成分名を保存しました")
        }
    }

    /** 成分選択ボタンの設定 */
    private fun setIngredientButton() {
        binding.adminIngredients.setOnClickListener { v ->
            val ingredients = vm.ingredients.value?.map { it.name }?.toTypedArray() ?: arrayOf()
            val checkedItems = BooleanArray(ingredients.size) // 初期状態ではすべての項目を未選択に設定

            AlertDialog.Builder(requireContext())
                .setTitle("成分を選択")
                .setMultiChoiceItems(ingredients, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    val selectedIngredients =
                        ingredients.filterIndexed { index, _ -> checkedItems[index] }
                    val selectedIngredientsPair = vm.ingredients.value?.mapNotNull {
                        if (selectedIngredients.contains(it.name)) {
                            it
                        } else {
                            null
                        }
                    }

                    vm.selectedIngredients.value = selectedIngredientsPair
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun settingKeyBord(editText: EditText) {
        editText.inputType = InputType.TYPE_CLASS_TEXT // Add this line
        editText.setOnKeyListener { _, keyCode, event ->  // Add this block
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardFrom(requireContext(), editText)
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboardFrom(requireContext(), binding.root)
    }
}
