package com.butterknife.compiler;

import com.butterknife.annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by zsd on 2018/2/23 17:52
 * desc:生成代码的一个类
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
    }

    //1.指定处理的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    //2.得到要处理的注解
    /**
     * 重写getSupportedAnnotationTypes方法
     * 通过重写该方法，告知Processor哪些注解需要处理
     *
     * @return 返回一个Set集合，集合内容为自定义注解的包名+类名
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }


    /**
     * 重写process方法
     * process方法表示 有注解的话就会进到这个方法中来
     * 所有的注解处理都是从这个方法开始的，你可以理解为，
     * 当APT找到所有需要处理的注解后，会回调这个方法，你可以通过这个方法的参数，拿到你所需要的信息。
     *
     * @param annotations 待处理的 Annotations
     * @param roundEnv    RoundEnvironment roundEnv ：表示当前或是之前的运行环境，可以通过该对象查找找到的注解
     * @return 表示这组 annotations 是否被这个 Processor 接受，如果接受（true）后续子的 pocessor 不会再对这个 Annotations 进行处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("----------process---------->");
        //所有被使用的@BindView.class
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        /**
         *  System.out.println("----------process---------->"+element.getSimpleName().toString());
         *  这个打印的情况很乱，就是说会把每个Activity的注解都打印下来
         *  比如在MianActivity中有注解如下
         *  BindView(R.id.tv1)
         *  TextView text1;
         *  BindView(R.id.tv2)
         *  TextView text2;
         *  在SecondActivity中有注解如下
         *  BindView(R.id.tv3)
         *  TextView text3;
         *  BindView(R.id.tv4)
         *  TextView text4;
         *  那么打印结果全部在一起了
         *  Console==================如下
         *  ----------process---------->text1
         *  ----------process---------->text2
         *  ----------process---------->text3
         *  ----------process---------->text4
         *  于是我们就无法区分这里找到的注解到底属于哪一个类所有
         *  我们要解决的就是一个activity对应一个List<Element>
         *  element.getEnclosingElement().getSimpleName().toString();
         *  上面这句话解析出来对应的就是注解相对应的那个activity
         */
        /*for (Element element : elements) {
            Element enclosingElement = element.getEnclosingElement();
            System.out.println("-------------------->"+element.getSimpleName().toString());
            System.out.println("-------------------->"+enclosingElement.getSimpleName().toString());

        }*/
        Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();
        for (Element element : elements) {//这里理解为拿到所有的被@BindView修饰的元素
            //拿到element所属是哪一个类 比如text1属于MainActivity，text3属于SecondActivity
            //enclosingElement就表示MainActivity或者SecondActivity……
            Element enclosingElement = element.getEnclosingElement();
            //拿到enclosingElement下面的被@BindView修饰的元素
            List<Element> viewBindElements = elementsMap.get(enclosingElement);
            if (viewBindElements == null) {
                viewBindElements = new ArrayList<>();
                elementsMap.put(enclosingElement, viewBindElements);
            }
            viewBindElements.add(element);
        }
        //生成代码
        for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
            Element enclosingElement = entry.getKey();
            List<Element> viewBindElements = entry.getValue();
            //现在我们要生成这样一个类xxxxx_ViewBinding文件
            String activityClassNameStr = enclosingElement.getSimpleName().toString();
            ClassName unbindClassName = ClassName.get("com.butterknife", "Unbinder");
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            //下面这行代码主要是为了拼接这样的一句话public final class xxxx_ViewBinding implements Unbinder
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)//指定类的属性为public final
                    .addField(activityClassName, "target", Modifier.PRIVATE)//添加属性
                    .addSuperinterface(unbindClassName);
            //实现Unbinder方法
            ClassName callSuperClassName = ClassName.get("android.support.annotation", "CallSuper");
            MethodSpec.Builder unbindMethodBuild = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(callSuperClassName)
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

            unbindMethodBuild.addStatement("$L target = this.target",activityClassName);
            unbindMethodBuild.addStatement("if (target == null) throw new IllegalStateException(\"Bindings already cleared.\")");

            unbindMethodBuild.addStatement("this.target = null");
            //构造函数
            //public ActivityLogin_ViewBinding(ActivityLogin target) {
            //this.target = target;
            //target.text = Utils.findViewById(source,R.id.text);
            //}
//            ClassName uiThreadClassName = ClassName.get("android.support.annotation", "UiThread");
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(activityClassName, "target");
            constructorMethodBuilder.addStatement("this.target = target");
            for (Element viewBindElement : viewBindElements) {
                String filedName = viewBindElement.getSimpleName().toString();
                ClassName classUtilsName = ClassName.get("com.butterknife", "Utils");
                int resId = viewBindElement.getAnnotation(BindView.class).value();
                constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target,$L)",filedName,classUtilsName,resId);
                unbindMethodBuild.addStatement("target.$L = null",filedName);
            }
            classBuilder.addMethod(constructorMethodBuilder.build());
            classBuilder.addMethod(unbindMethodBuild.build());
            //生成类
            try {
                //获取包名
                String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("buttknife自动生成的类")
                        .build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("生成类过程异常");
            }
        }
        return false;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        //在这里将我们写的注解添加进来 目前我们只写了一个BindView注解
        annotations.add(BindView.class);
        return annotations;
    }
}
